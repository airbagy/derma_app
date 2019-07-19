package org.tensorflow.lite.examples.classification;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.util.Log;
import java.io.File;
import android.provider.MediaStore;
import android.database.Cursor;
import android.os.Build;
import java.io.IOException;
import android.os.Environment;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import org.tensorflow.lite.examples.classification.tflite.Classifier;

import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import org.tensorflow.lite.examples.classification.env.BorderedText;
import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.tflite.Classifier;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;

public class MainActivity extends Activity {

    public static final int GALLERY_REQUEST_CODE = 0;
    private String currentPhotoPath = "";
    public static final int CAMERA_REQUEST_CODE = 1;
    private static final Logger LOGGER = new Logger();
    private Classifier classifier;
    private Model model = Model.FLOAT;
    private Device device = Device.CPU;
    private Bitmap resultmodel_img = null;
    private int numThreads = -1;
    private ResultModel image_status;

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

    protected void processImage(Bitmap image) {
        recreateClassifier(model, device, numThreads);
        Bitmap rgbBitmap = getRGB(image);
        Bitmap croppedBitmap = Bitmap.createBitmap(
                classifier.getImageSizeX(), classifier.getImageSizeY(), Config.ARGB_8888);
        Matrix frameToCropTransform = ImageUtils.getTransformationMatrix(
                image.getWidth(),
                image.getHeight(),
                classifier.getImageSizeX(),
                classifier.getImageSizeY(),
                0,
                true);
        Matrix cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
        Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbBitmap, frameToCropTransform, null);

        System.out.println("ProcessImage");
        if (classifier != null) {
            final long startTime = SystemClock.uptimeMillis();
            final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
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
            classifier = Classifier.create(this, model, device, numThreads);
        } catch (IOException e) {
            LOGGER.e(e, "Failed to create classifier.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void activateCamera(View v) {
        Intent intent = new Intent(this, ClassifierActivity.class);
        startActivity(intent);


    }

    public void mainGotoAbout(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void accessSharing(View v){
        Intent intent = new Intent(this, SharingActivity.class);
        startActivity(intent);
    }

    public void accessLibrary(View v){
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        String[] mimetypes = {"image/jpeg", "image/png"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent.createChooser(intent, "Select your image sample."), GALLERY_REQUEST_CODE);
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    public void processCameraImage(View v) {

        Intent cameraIntent = new
                Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    public String getStatusFromURI(Uri contentUri, Bitmap cropped_img, int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            String res = null;
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
            return "firststagedone";
        }
        return "incomplete";
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case GALLERY_REQUEST_CODE:
//                    System.out.println("gallery");
//                    System.out.println(data.getData());
//                    if (data.getData() != null) {
//                        Uri imageURI = data.getData();
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
//                            CropImage.activity(imageURI)
//                                    .start(this);
//                            processImage(bitmap);
//                        }
//                        catch (Exception e){
//                            System.out.println("Cannot process image");
//                        }
////                        String path = null;
////                        String [] files = {MediaStore.MediaColumns.DATA};
////                        Cursor cursor = getContentResolver().query(imageURI, files, null, null, null);
////                        if (cursor.moveToFirst()) {
////                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
////                           path = cursor.getString(column_index);
////                        }
////                        BitmapFactory.Options options = new BitmapFactory.Options();
////                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
////                        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
////                        processImage(bitmap);
////                        cursor.close();
//                    }
//                    break;
//
//                case CAMERA_REQUEST_CODE:
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    processImage(photo);
//            }
//
//            resultmodel_img  = image_status.getOriginalBitmap();
//        }
//    }

    public void accessMaps(View v) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }

    public void activateInfoPage(View v){
        Intent intent = new Intent(this, InfoPageActivity.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    processImage(photo);
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                case GALLERY_REQUEST_CODE:
                    if (data.getData() != null) {
                        try {
                            Uri sourceUri = data.getData();
//                            File file = getImageFile();
//                            Uri destinationUri = Uri.fromFile(file);

                            Log.d("imageURI",sourceUri.toString());
                            CropImage.activity(sourceUri)
                                    .start(this);

//                            File file = new File(getPathFromURI(imageURI));
////                            if (file.exists()) {
////                                Log.d("EXISTS",imageURI.toString());
////                            }

//                            openCropActivity(sourceUri, sourceUri);
                        } catch (Exception e) {

                        }


//                        Intent intent = new Intent(this, ClassifierActivity.class);
//                        intent.putExtra("imageURI", imageURI.toString());
//                        startActivity(intent);
                    }
                    break;

            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

}

//    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
//        UCrop.Options options = new UCrop.Options();
//        options.setCircleDimmedLayer(true);
//        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
//        UCrop.of(sourceUri, destinationUri)
//                .withMaxResultSize(100, 100)
//                .withAspectRatio(5f, 5f)
//                .start(this);
//    }
