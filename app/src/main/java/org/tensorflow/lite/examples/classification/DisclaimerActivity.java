package org.tensorflow.lite.examples.classification;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class DisclaimerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disclaimer_page);
    }

    public void consentAction(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}