package com.derma.app.activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.derma.app.R;


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