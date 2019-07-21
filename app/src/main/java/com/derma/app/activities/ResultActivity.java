package com.derma.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.derma.app.R;

import com.derma.app.classifier.ResultModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private ResultModel resultModel;

    private LinearLayout ll;

    private String resultPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultModel = ResultModel.getInstance();
        setContentView(R.layout.results_page);
        ll = findViewById(R.id.resultDisplay);

        Map<String, Float> nvResults = resultModel.getNvResult();
        Map<String, Float> cancerResults = resultModel.getCancerResult();

        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageBitmap(resultModel.getImg_cropped());
        final float scale = getResources().getDisplayMetrics().density;
        int ivwidth  = (int) (200 * scale);
        int ivheight = (int) (150 * scale);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ivwidth, ivheight);
        lp.gravity = Gravity.CENTER;
        iv.setLayoutParams(lp);

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView resultView = new TextView(getApplicationContext());
        TextView infoView = new TextView(getApplicationContext());

        String text = "Your Nevi results were: ";
        String condition = "";
        Float max = 0f;
        for (Map.Entry<String, Float> entry: nvResults.entrySet()){
            text += entry.getKey() + ": " + (entry.getValue() * 100) + "% ";
        }
        text += "\n";
        if (cancerResults != null){
            text += "Since your Dysplastic Nevi confidence was high, your most likely skin condition is: ";
            for (Map.Entry<String, Float> entry: cancerResults.entrySet()){
                if (entry.getValue() > max){
                    condition = entry.getKey();
                    max = entry.getValue();
                }
            }
            max *= 100;
            text += condition + " with " + max.toString() + "% confidence.\n";
        }
        else {
            text += "Since your Melanocytic Nevi confidence was high, your skin" +
                    " likely has a benign mole, or no averse conditions!";
        }
        resultView.setBackgroundResource(R.drawable.round_textbox);
        resultView.setText(text);
        resultView.setLayoutParams(lp);
        resultView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        resultView.setTextColor(Color.BLACK);
        resultView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        resultView.setGravity(Gravity.CENTER);

        infoView.setBackgroundResource(R.drawable.round_textbox);

        if (cancerResults == null){
            infoView.setText(R.string.Melanocytic);
        }
        if (condition == "Actinic keratoses"){
            infoView.setText(R.string.Keratosis);
        }
        if (condition == "Basal cell carcinoma"){
            infoView.setText(R.string.BasalCell);
        }
        if (condition == "Benign keratosis-like lesions"){
            infoView.setText(R.string.Seborrheic);
        }
        if (condition == "Dermatofibroma"){
            infoView.setText(R.string.Dermatofibroma);
        }
        if (condition == "Melanoma"){
            infoView.setText(R.string.Melanoma);
        }
        if (condition == "Vascular lesions"){
            infoView.setText(R.string.Pyogenic);
        }
        infoView.setLayoutParams(lp);
        infoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        infoView.setTextColor(Color.BLACK);
        infoView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        infoView.setGravity(Gravity.CENTER);

        ll.addView(iv);
        ll.addView(resultView);
        ll.addView(infoView);
    }

    public void MainMenu(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void ShareActivity(View v){
        Intent intent = new Intent(this, SharingActivity.class);
        startActivity(intent);
    }

    private File createImageFile() throws IOException {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(date);
        String imageFileName = "sample_" + timeStamp;
        String storageDirectory;
        File storageDir;

        storageDirectory =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/Derma_Results";
        storageDir = new File(storageDirectory);
        if (!storageDir.exists()){
            storageDir.mkdir();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        resultPath = image.getAbsolutePath();
        return image;
    }
}
