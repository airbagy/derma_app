package com.derma.app.activities;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.mail.internet.MimeMultipart;
import com.derma.app.R;


public class ReceiveActivity extends Activity {

    private static final int PICKFILE_CODE = 1001;

    private String secretCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_page);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void recvAction(View v) {
        String code = ((EditText)findViewById(R.id.recv_code)).getText().toString();
        secretCode = code;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("text/*");
        startActivityForResult(intent, PICKFILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        try {
            if (requestCode == PICKFILE_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri uri = resultIntent.getData();
                    InputStream stream = getContentResolver().openInputStream(uri);
                    byte[] bytes = readBytes(stream);
                    String str = new String(bytes, "UTF-8");
                    byte[] data = decodeString(str, secretCode);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ImageView iv = (ImageView) findViewById(R.id.recv_result);
                    iv.setImageBitmap(bitmap);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }


    private byte[] decodeString(String str, String code) {
        try {
            Key key = generateKey(code);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = decodeString64(str);
            byte[] decValue = c.doFinal(decodedValue);
            return decValue;
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

    private byte[] decodeString64(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

}
