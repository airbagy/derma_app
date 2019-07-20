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

package com.derma.app.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

import com.derma.app.classifier.env.Logger;
import com.derma.app.classifier.tflite.Classifier;
import com.derma.app.classifier.tflite.Classifier.Device;
import com.derma.app.classifier.tflite.Classifier.Model;

public class ImageClassifierActivity extends Activity {
    private static final Logger LOGGER = new Logger();
    private Classifier classifier;
    private Model model = Model.QUANTIZED;
    private Device device = Device.CPU;
    private int numThreads = -1;


    protected void processImage(Bitmap image) {
        System.out.println("ProcessImage");
        recreateClassifier(model, device, numThreads);
        if (classifier != null) {
            final long startTime = SystemClock.uptimeMillis();
            final List<Classifier.Recognition> results = classifier.recognizeImage(image);
            System.out.println(results);
        }
    }

    private void recreateClassifier(Model model, Device device, int numThreads) {
        if (classifier != null) {
            LOGGER.d("Closing classifier.");
            classifier.close();
            classifier = null;
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
            classifier = Classifier.create(this, model, device, numThreads, Classifier.ClassifierType.CANCER);
        } catch (IOException e) {
            LOGGER.e(e, "Failed to create classifier.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        System.out.println("imageclassactivity");
        if(extras != null){
            Bitmap image = extras.getParcelable("bitmap");
            processImage(image);
        }
    }
}
