package activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.derma.app.R;

import org.tensorflow.lite.examples.classification.ResultModel;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private ResultModel resultModel;

    private LinearLayout ll;

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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        iv.setLayoutParams(lp);

        TextView resultView = new TextView(getApplicationContext());
        TextView infoView = new TextView(getApplicationContext());

        String text = "Your NV results were: ";
        String condition = "";
        Float max = 0f;
        for (Map.Entry<String, Float> entry: nvResults.entrySet()){
            text += entry.getKey() + ": " + (entry.getValue() * 100) + "% ";
        }
        text += "\n";
        if (cancerResults != null){
            text += "Since your NV confidence was high, your most likely skin condition is: ";
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
            text += "Since your non-NV confidence was high, your skin likely has no averse conditions!";
        }
        resultView.setBackgroundResource(R.drawable.round_textbox);
        resultView.setText(text);
        resultView.setLayoutParams(lp);
        resultView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        resultView.setTextColor(Color.BLACK);
        resultView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        resultView.setGravity(Gravity.CENTER);

        infoView.setBackgroundResource(R.drawable.round_textbox);

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
        if (condition == "Melanocytic nevi"){
            infoView.setText(R.string.Melanocytic);
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
}
