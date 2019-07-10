package org.tensorflow.lite.examples.classification;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import android.widget.ImageView;

public class MainActivity extends Activity {

    public static final int GALLERY_REQUEST_CODE = 0;

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

    public void accessLibrary(View v){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        String[] mimetypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select your image sample."), GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    if(data.getData() != null){
                        Uri imageURI = data.getData();

                        Intent intent = new Intent(this, ClassifierActivity.class);
                        intent.putExtra("imageURI", imageURI.toString());
                        startActivity(intent);
                    }
                    break;

            }
    }

    public void activateInfoPage(View v){
        Intent intent = new Intent(this, InfoPageActivity.class);
        startActivity(intent);
    }

}