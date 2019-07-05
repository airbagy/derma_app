package org.tensorflow.lite.examples.classification;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class InfoPageActivity extends Activity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_page);

        //Spinner spinner = (Spinner) findViewById(R.id.spinner);
    }

    public void consentAction(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
