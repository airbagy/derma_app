package com.derma.app.activities;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.*;
import com.derma.app.R;


public class SharingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to share your result?")
                .setTitle("Warning")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#87CEFA"));
            }
        });
        dialog.show();
//        setContentView(R.layout.sharing_page);
    }

    public void sendAction(View v) {
        String address = ((EditText)findViewById(R.id.send_address)).getText().toString();
        String subject = ((EditText)findViewById(R.id.send_subject)).getText().toString();
        String message = ((EditText)findViewById(R.id.send_message)).getText().toString();
        String code = ((EditText)findViewById(R.id.send_code)).getText().toString();

        String encodedMessage = encodeString(message, code);

        sendEmail(address, subject, encodedMessage);

        TextView result = (TextView)findViewById(R.id.send_result);
        result.setText("Sent " + subject);

    }

    private void sendEmail(String address, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ address });
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");

        // try to return to this app
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(Intent.createChooser(intent, "Choose an email client:"));
    }

    private String encodeString(String str, String code) {
        try {
            Key key = generateKey(code);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = c.doFinal(str.getBytes());
            return encodeString64(encValue);
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

}
