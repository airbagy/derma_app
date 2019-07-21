package com.derma.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.WrapperListAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.derma.app.R;

import com.derma.app.classifier.ResultModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        lp.setMargins(10,10,10,10);
        iv.setLayoutParams(lp);

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView resultView = new TextView(getApplicationContext());
        TextView infoView = new TextView(getApplicationContext());
        lp.setMargins(10, 10, 10, 10);

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
        resultView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
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
        infoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
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
        String imageFileName = "derma_result_" + timeStamp;
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

    public void saveResult(View v) {
        AlertDialog dialog;
        AlertDialog.Builder builder;
        LayoutInflater factory;
        Context ctx = this;

        builder = new AlertDialog.Builder(this);
        factory = LayoutInflater.from(this);
        builder.setTitle("Save Results?")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(saveImage()){
                            Toast.makeText(ctx, "Results Saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ctx, "Save Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
    }

    private boolean saveImage() {
        Bitmap bitmap = Bitmap.createBitmap(ll.getWidth(),
                ll.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ll.draw(canvas);
        try{
            File resultFile = createImageFile();
            FileOutputStream os = new FileOutputStream(resultFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            Uri resultUri = Uri.fromFile(resultFile);
            resultModel.setResultImageUri(resultUri);
            if (!resultFile.exists()){
                return false;
            }
            return true;
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
    }
}
