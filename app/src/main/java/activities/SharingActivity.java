package activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.derma.app.R;

import javax.mail.*;


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

        sendEmail(address, subject, message);

        TextView result = (TextView)findViewById(R.id.send_result);
        result.setText("Sent " + subject);

    }

    public void recvAction(View v) {
        String address = ((EditText)findViewById(R.id.recv_address)).getText().toString();
        String password = ((EditText)findViewById(R.id.recv_password)).getText().toString();
        String subject = ((EditText)findViewById(R.id.recv_subject)).getText().toString();
        String code = ((EditText)findViewById(R.id.recv_code)).getText().toString();

        recvEmail(address, password, subject);

        TextView result = (TextView)findViewById(R.id.recv_result);
        result.setText("Received " + subject);
    }

    public void sendEmail(String address, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ address });
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");

        // try to return to this app
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(Intent.createChooser(intent, "Choose an email client:"));
    }

    public void recvEmail(String address, String password, String subject) {
        return;
    }

}