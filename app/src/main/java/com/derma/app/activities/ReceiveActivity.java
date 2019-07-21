package com.derma.app.activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.*;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.mail.internet.MimeMultipart;
import com.derma.app.R;


public class ReceiveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_page);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void recvAction(View v) {
        String address = ((EditText)findViewById(R.id.recv_address)).getText().toString();
        String password = ((EditText)findViewById(R.id.recv_password)).getText().toString();
        String subject = ((EditText)findViewById(R.id.recv_subject)).getText().toString();
        String code = ((EditText)findViewById(R.id.recv_code)).getText().toString();

        String status = "";
        try {
            String message = recvEmail(address, password, subject);
            if (message != null) {
                String decodedMessage = decodeString(message, code);
                status = "Received " + subject + ": '" + decodedMessage + "'";
            } else {
                status = "No message for subject " + subject;
            }
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
        }

        TextView result = (TextView)findViewById(R.id.recv_result);
        result.setText(status);
    }

    private String decodeString(String str, String code) {
        try {
            Key key = generateKey(code);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = decodeString64(str);
            byte[] decValue = c.doFinal(decodedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
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

    private String recvEmail(String address, String password, String subject) throws Exception {

        String host = "pop." + address.substring(address.indexOf("@") + 1);

        Properties properties = new Properties();

        properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.setProperty("mail.pop3.port", "995");
        properties.setProperty("mail.pop3.socketFactory.port", "995");

        URLName url = new URLName("pop3", host, 995, "",
                "recent:" + address, password);

        Session session = Session.getInstance(properties, null);
        Store store = session.getStore(url);
        store.connect();


        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        Message[] messages = emailFolder.getMessages();
        String message = recvMessage(messages, 10, subject);

        emailFolder.close(false);
        store.close();

        return message;
    }

    private String recvMessage(Message[] messages, int maxLen, String subject) throws Exception {
        int numMessages = messages.length;
        int len = Math.min(maxLen, numMessages);
        for (int i = numMessages - len; i < numMessages; i++) {
            Message message = messages[i];
            String messageSubject = message.getSubject();
            if (messageSubject.equals(subject)) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                String result = (String) (mimeMultipart.getBodyPart(0).getContent());
                return result;
            }
        }
        return null;
    }

}
