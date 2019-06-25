package org.tensorflow.lite.examples.classification;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_message_android);
    }

    public void dismissWelcomeMessageBox(View v) {
        Intent intent = new Intent(this, ClassifierActivity.class);
        startActivity(intent);
    }

}