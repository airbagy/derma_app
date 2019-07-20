/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package activities;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.derma.app.R;

import org.tensorflow.lite.examples.classification.ResultModel;
import org.tensorflow.lite.examples.classification.Stage;
import org.tensorflow.lite.examples.classification.env.BorderedText;
import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.tflite.Classifier;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.ClassifierType;
import org.tensorflow.lite.examples.classification.tflite.ClassifierSkinDetNet;

import activities.CameraActivity;

public class ClassifierActivity extends AppCompatActivity {
  private static final Logger LOGGER = new Logger();

  private Classifier cancerclassifier;
  private Classifier nvclassifier;
  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  public static final int CAMERA_REQUEST_CODE = 1;
  private Model model = Model.QUANTIZED;

  private Device device = Device.CPU;
  private int numThreads = -1;

  private ResultModel resultModel;

  private Bitmap convertBitmap(Bitmap bitmap){
    Bitmap converted = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas();
    canvas.setBitmap(converted);
    Paint paint = new Paint();
    paint.setFilterBitmap(true);
    canvas.drawBitmap(bitmap, 0, 0, paint);
    return converted;
  }

  private Bitmap getRGB(Bitmap src){
    int [] colors = new int[src.getWidth() * src.getHeight()];
    src.getPixels(colors, 0, src.getWidth(), 0 ,0, src.getWidth(), src.getHeight());
    Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
    result.setPixels(colors, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
    return result;
  }

  protected void ClassifyNV(Bitmap image) {
    recreateClassifier(model, device, numThreads);
    Bitmap rgbBitmap = getRGB(image);
    Bitmap croppedBitmap = Bitmap.createBitmap(
            nvclassifier.getImageSizeX(), nvclassifier.getImageSizeY(), Config.ARGB_8888);
    Matrix frameToCropTransform = ImageUtils.getTransformationMatrix(
            image.getWidth(),
            image.getHeight(),
            nvclassifier.getImageSizeX(),
            nvclassifier.getImageSizeY(),
            0,
            true);
    Matrix cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);
    Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbBitmap, frameToCropTransform, null);

    System.out.println("nvclassifier");
    if (nvclassifier != null) {
      final long startTime = SystemClock.uptimeMillis();
      final List<Classifier.Recognition> results = nvclassifier.recognizeImage(croppedBitmap);
      System.out.println(results);
      Map<String, Float> resultDict = new HashMap<>();
      for (int i = 0; i < results.size(); i++){
        resultDict.put(results.get(i).getTitle(), results.get(i).getConfidence());
      }
      resultModel.setNvResult(resultDict);
      resultModel.setStage(Stage.NV_ClASSIFIED);
      if (results.get(0).getConfidence() >= results.get(1).getConfidence()){
        ClassifyCancer(image);
      }
      else {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
      }
    }
  }

  protected void ClassifyCancer(Bitmap image) {
    recreateClassifier(model, device, numThreads);
    Bitmap rgbBitmap = getRGB(image);
    Bitmap croppedBitmap = Bitmap.createBitmap(
            cancerclassifier.getImageSizeX(), cancerclassifier.getImageSizeY(), Config.ARGB_8888);
    Matrix frameToCropTransform = ImageUtils.getTransformationMatrix(
            image.getWidth(),
            image.getHeight(),
            cancerclassifier.getImageSizeX(),
            cancerclassifier.getImageSizeY(),
            0,
            true);
    Matrix cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);
    Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbBitmap, frameToCropTransform, null);

    System.out.println("cancerclassifier");
    if (cancerclassifier != null) {
      final long startTime = SystemClock.uptimeMillis();
      final List<Classifier.Recognition> results = cancerclassifier.recognizeImage(croppedBitmap);
      System.out.println(results);
      Map<String, Float> resultDict = new HashMap<>();
      for (int i = 0; i < results.size(); i++){
        resultDict.put(results.get(i).getTitle(), results.get(i).getConfidence());
      }
      resultModel.setCancerResult(resultDict);
      resultModel.setStage(Stage.CLASSIFIED);
      Intent intent = new Intent(this, ResultActivity.class);
      startActivity(intent);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    resultModel = ResultModel.getInstance();
    if (resultModel.getStage() == Stage.CROPPED){
      ClassifyNV(resultModel.getImg_cropped());
    }
  }

  private void recreateClassifier(Model model, Device device, int numThreads) {
    if (nvclassifier != null) {
      LOGGER.d("Closing NV classifier.");
      nvclassifier.close();
      nvclassifier = null;
    }
    if (cancerclassifier != null) {
      LOGGER.d("Closing Cancer classifier.");
      cancerclassifier.close();
      cancerclassifier = null;
    }
    if (device == Device.GPU && model == Model.QUANTIZED) {
      LOGGER.d("Not creating classifier: GPU doesn't support quantized models.");
      runOnUiThread(
          () -> {
            Toast.makeText(this, "GPU does not yet supported quantized models.", Toast.LENGTH_LONG)
                .show();
          });
      return;
    }
    try {
      LOGGER.d(
          "Creating classifier (model=%s, device=%s, numThreads=%d)", model, device, numThreads);
      nvclassifier = Classifier.create(this, model, device, numThreads, ClassifierType.NV);
      cancerclassifier = Classifier.create(this, model, device, numThreads, ClassifierType.CANCER);
    } catch (IOException e) {
      LOGGER.e(e, "Failed to create classifier.");
    }
  }
}
