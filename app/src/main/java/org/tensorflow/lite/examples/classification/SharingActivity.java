package org.tensorflow.lite.examples.classification;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Properties;
import javax.mail.*;
import javax.crypto.*;


public class SharingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharing_page);
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
        // add encryption as well
        return encodeString64(str);
    }

    private String decodeString(String str, String code) {
        // add decryption as well
        return decodeString64(str);
    }

    private String encodeString64(String str) {
        try {
            Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception e) {}
        return null;
    }

    private String decodeString64(String str) {
        try {
            return new String(Base64.decode(str, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {}
        return null;
    }

    private String recvEmail(String address, String password, String subject) throws Exception {
        String host = "pop.gmail.com";
        Properties properties = new Properties();

        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        Store store = emailSession.getStore("pop3s");

        store.connect(host, address, password);
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        Message[] messages = emailFolder.getMessages();
        String message = recvMessage(messages, 10, subject);

        emailFolder.close(false);
        store.close();

        return message;
    }

    private String recvMessage(Message[] messages, int maxLen, String subject) throws Exception {
        int len = Math.min(maxLen, messages.length);
        for (int i = 0; i < len; i++) {
            Message message = messages[i];
            if (message.getSubject().equals(subject)) {
                return message.getContent().toString();
            }
        }
        return null;
    }

}