package com.derma.app.activities;

import android.Manifest;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import android.provider.MediaStore;
import android.os.Build;
import java.io.IOException;
import android.os.Environment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.graphics.Bitmap;

import com.derma.app.classifier.ResultModel;
import com.derma.app.classifier.Stage;
import com.derma.app.classifier.tflite.Classifier;

import com.derma.app.R;

import com.derma.app.classifier.env.Logger;
import com.derma.app.classifier.tflite.Classifier.Device;
import com.derma.app.classifier.tflite.Classifier.Model;

import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    public static final int GRANT_CAMERA_PERMISSION = 1;
    public static final int GALLERY_REQUEST_CODE = 0;
    private String currentPhotoPath = "";
    private String imageFilePath = "";
    public static final int CAMERA_REQUEST_CODE = 1;
    private static final Logger LOGGER = new Logger();
    private Classifier classifier;
    private Model model = Model.FLOAT;
    private Device device = Device.CPU;
    private Bitmap resultmodel_img = null;
    private int numThreads = -1;
    private ResultModel resultModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultModel = ResultModel.getInstance();
        resultModel.clearResultModel();
        setContentView(R.layout.main_menu);
    }

    public void mainGotoAbout(View v) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void accessSharing(View v){
        Intent intent = new Intent(this, SharingActivity.class);
        startActivity(intent);
    }

    public void activateInfoPage(View v){
        Intent intent = new Intent(this, InfoPageActivity.class);
        startActivity(intent);
    }

    private void StartClassification(){
        Intent intent = new Intent(this, ClassifierActivity.class);
        startActivity(intent);
    }

    public void accessLibrary(View v){
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("For best result, lease take the photo in a well-lighted place!")
                .setTitle("Important")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#87CEFA"));
            }
        });
        dialog.show();

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        GRANT_CAMERA_PERMISSION);
//            }
//            else {
//                // No explanation needed; request the permission
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        GRANT_CAMERA_PERMISSION);
//            }
//        }
//        else{
//            Intent cameraIntent = new
//                    Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//        }
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GRANT_CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new
                            Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
                else {
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return;
            }
        }
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

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            Uri sourceUri;
            Bitmap photo;
            Activity act;
            LayoutInflater factory;
            View view;
            AlertDialog dialog;

            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    photo = (Bitmap) data.getExtras().get("data");
                    Intent intent = new Intent(this, ClassifierActivity.class);
                    sourceUri = getImageUri(getApplicationContext(), photo);
                    resultModel.setImg_org(photo);
                    resultModel.setUri_org(sourceUri);
                    resultModel.setStage(Stage.ORIGINAL);

                    Log.d("imageURI",sourceUri.toString());

                    act = this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    factory = LayoutInflater.from(this);
                    view = factory.inflate(R.layout.alert_dialog, null);
                    builder.setView(view)
                            .setMessage("Please make sure the area of lesion is in the center grid!")
                            .setTitle("Important")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    CropImage.activity(sourceUri)
                                            .setAspectRatio(3,4)
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .setBackgroundColor(Color.parseColor("#73666666"))
                                            .start(act);
                                }
                            });

                    dialog = builder.create();
                    dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#87CEFA"));
                        }
                    });
                    dialog.show();
                    break;

                case GALLERY_REQUEST_CODE:
                    if (data.getData() != null) {
                        try {
                            sourceUri = data.getData();
                            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                    sourceUri);
                            Log.d("imageURI",sourceUri.toString());
                            resultModel.setImg_org(photo);
                            resultModel.setUri_org(sourceUri);
                            resultModel.setStage(Stage.ORIGINAL);

                            act = this;
                            builder = new AlertDialog.Builder(this);
                            factory = LayoutInflater.from(this);
                            view = factory.inflate(R.layout.alert_dialog, null);
                            builder.setView(view)
                                    .setMessage("Please make sure the area of lesion is in the center grid!")
                                    .setTitle("Important")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, just close
                                            // the dialog box and do nothing
                                            CropImage.activity(sourceUri)
                                                    .setAspectRatio(3,4)
                                                    .setGuidelines(CropImageView.Guidelines.ON)
                                                    .setBackgroundColor(Color.parseColor("#73666666"))
                                                    .start(act);
                                        }
                                    });

                            dialog = builder.create();
                            dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#87CEFA"));
                                }
                            });
                            dialog.show();

//                            TextView titleView = (TextView) dialog.findViewById(android.R.id.title);
//                            titleView.setTextSize(16);
//                            TextView msgView = (TextView) dialog.findViewById(android.R.id.message);
//                            msgView.setTextSize(16);
//                            Button btn1 = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                            btn1.setTextSize(16);
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        try{
                            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                    resultUri);
                            resultModel.setImg_cropped(photo);
                            resultModel.setUri_cropped(resultUri);
                            resultModel.setStage(Stage.CROPPED);
                            StartClassification();
                        }
                        catch(Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                    break;

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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