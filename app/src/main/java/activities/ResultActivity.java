package activities;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
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
        ll = findViewById(R.id.resultDisplay);
        setContentView(R.layout.results_page);

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
            text += entry.getKey() + ": " + entry.getValue().toString() + "% ";
        }
        text += "\n";
        if (cancerResults != null){
            text += "Since your NV confidence was high, your most likely skin condition is: ";
            for (Map.Entry<String, Float> entry: nvResults.entrySet()){
                if (entry.getValue() > max){
                    condition = entry.getKey();
                    max = entry.getValue();
                }
            }
            text += condition + " with " + max.toString() + "% confidence.\n";
        }
        else {
            text += "Since your non-NV confidence was high, your skin likely has no averse conditions!";
        }

        resultView.setText(text);
        resultView.setLayoutParams(lp);

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

        ll.addView(iv);
        ll.addView(resultView);
        ll.addView(infoView);
    }
}
