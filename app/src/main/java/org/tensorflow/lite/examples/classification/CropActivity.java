package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Activity;
import android.support.v4.app.Fragment;

import com.yalantis.ucrop.UCrop;

public class CropActivity extends Activity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String URIString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                URIString= null;
            } else {
                URIString= extras.getString("imageURI");
            }
        } else {
            URIString = (String) savedInstanceState.getSerializable("imageURI");
        }

        UCrop.Options cropOptions = new UCrop.Options();
        UCrop uCrop = UCrop.of(Uri.parse(URIString), Uri.parse(URIString));
        uCrop.withOptions(cropOptions);
        uCrop.start(this);


    }
}
