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
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
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
import androidx.core.content.FileProvider;

import com.derma.app.R;

import com.derma.app.classifier.ResultModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ResultActivity extends AppCompatActivity {

    private ResultModel resultModel;

    private LinearLayout ll;

    private String resultPath;

    private Uri globalUri;

    private String code;

    private TextView classifierResult;

    private TextView infoView;

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        resultModel = ResultModel.getInstance();
        setContentView(R.layout.results_page);
        ll = findViewById(R.id.resultDisplay);

        Map<String, Float> nvResults = resultModel.getNvResult();
        Map<String, Float> cancerResults = resultModel.getCancerResult();

        iv = new ImageView(getApplicationContext());
        iv.setImageBitmap(resultModel.getImg_cropped());
        final float scale = getResources().getDisplayMetrics().density;
        int ivwidth  = (int) (400 * 0.6 * scale);
        int ivheight = (int) (300 * 0.6 * scale);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ivwidth, ivheight);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(10,10,10,10);
        iv.setLayoutParams(lp);

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        classifierResult = new TextView(getApplicationContext());
        infoView = new TextView(getApplicationContext());
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
        classifierResult.setBackgroundResource(R.drawable.round_textbox);
        classifierResult.setText(text);
        classifierResult.setLayoutParams(lp);
        classifierResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        classifierResult.setTextColor(Color.BLACK);
        classifierResult.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        classifierResult.setGravity(Gravity.CENTER);

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
        ll.addView(classifierResult);
        ll.addView(infoView);
    }

    public void MainMenu(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void ShareActivity(View v){
        AlertDialog dialog_1, dialog_2;
        AlertDialog.Builder builder_1, builder_2;
        Context ctx = this;


        builder_2 = new AlertDialog.Builder(this);
        builder_2.setTitle("Security Code")
                .setMessage("xxxxx")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        intent.putExtra(Intent.EXTRA_STREAM, globalUri);

                        //File encryptedFile = new File(resultPath);
                        //Uri uri  = FileProvider.getUriForFile(ctx, "com.derma.app.fileprovider", encryptedFile);
                        //intent.setDataAndType(uri, ctx.getContentResolver().getType(uri));
                        // try to return to this app
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(Intent.createChooser(intent, "Choose a platform:"));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialog_2 = builder_2.create();
        dialog_2.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog_2.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#87CEFA"));
            }
        });

        builder_1 = new AlertDialog.Builder(this);
        builder_1.setTitle("Share Result?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        code = shareImage();
                        if(code != null){
                            dialog.cancel();

                            dialog_2.show();
                            TextView messageView = (TextView)dialog_2.findViewById(android.R.id.message);
                            messageView.setText("Your secure share code is: "  + code);
                        } else {
                            Toast.makeText(ctx, "Cannot create secure file.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog_1 = builder_1.create();
        dialog_1.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog_1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#87CEFA"));
            }
        });
        dialog_1.show();

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
                        dialog.cancel();
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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final float scale = getResources().getDisplayMetrics().density;
        lp.gravity = Gravity.CENTER;
        lp.setMargins(10,10,10,10);
        iv.setLayoutParams(lp);
        iv.setImageBitmap(resultModel.getImg_cropped());

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
            int ivwidth  = (int) (400 * 0.6 * scale);
            int ivheight = (int) (300 * 0.6 * scale);
            lp = new LinearLayout.LayoutParams(ivwidth, ivheight);
            lp.gravity = Gravity.CENTER;
            lp.setMargins(10,10,10,10);
            iv.setLayoutParams(lp);
            iv.setImageBitmap(resultModel.getImg_cropped());

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

    private String shareImage() {

        Bitmap bitmap = Bitmap.createBitmap(ll.getWidth(),
                ll.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ll.draw(canvas);
        try{
            File resultFile = createEncodedFile();
            FileOutputStream os = new FileOutputStream(resultFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] ba = baos.toByteArray();
            String code = random5Char();
            byte[] bae = encodeBytes(ba, code);

            os.write(bae);
            os.flush();
            os.close();
            Uri resultUri = Uri.fromFile(resultFile);
            globalUri = resultUri;
//            resultModel.setResultImageUri(resultUri);



            if (!resultFile.exists()){
                return null;
            }
            return code;
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }

    }

    private File createEncodedFile() throws IOException {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(date);
        String imageFileName = "derma_result_encoded" + timeStamp;
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
                ".txt",         /* suffix */
                storageDir      /* directory */
        );

        resultPath = image.getAbsolutePath();
        return image;
    }

    private byte[] encodeBytes(byte[] ba, String code) {
        try {
            Key key = generateKey(code);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = c.doFinal(ba);
            return encodeString64(encValue).getBytes();
        } catch (Exception e) {
            return null;
        }
    }

    private static Key generateKey(String code) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] keyBytes = sha.digest(code.getBytes("UTF-8"));
        keyBytes = Arrays.copyOf(keyBytes, 16);
        Key key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    private String encodeString64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private String random5Char() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = 5;
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            if (i % 3 == 0) {
                tempChar = (char) (generator.nextInt(26) + 97);
            } else {
                tempChar = (char) (generator.nextInt(9) + 48);
            }

            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}

