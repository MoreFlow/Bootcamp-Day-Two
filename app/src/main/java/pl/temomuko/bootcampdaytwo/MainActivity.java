package pl.temomuko.bootcampdaytwo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edit_mail)
    EditText mailEditText;

    @BindView(R.id.edit_topic)
    EditText topicEditText;

    @BindView(R.id.edit_message)
    EditText messageEditText;

    @BindView(R.id.fab_send)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handleIntent();
    }

    public void handleIntent() {
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            uri = uri.substring(9);
            uri = uri.replace('_', '.');
            uri = String.format("%s", uri + getString(R.string.droids_mail_suffix));
            mailEditText.setText(uri);
        }
    }

    @OnClick(R.id.fab_send)
    public void send() {
        if (!validateEmptyInput()) {
            return;
        }
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = String.format("mailto:%s?subject=%s&body=%s",
                Uri.encode(mailEditText.getText().toString().trim()),
                Uri.encode(topicEditText.getText().toString()),
                Uri.encode(messageEditText.getText().toString()));
        Uri uri = Uri.parse(uriText);

        send.setData(uri);

        PackageManager manager = getPackageManager();
        for (ResolveInfo info : manager.queryIntentActivities(send, 0)) {
            if (info.activityInfo.packageName.equals(getString(R.string.gmail_package_name))) {
                send.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                startActivity(send);
                return;
            }
        }
        startActivity(Intent.createChooser(send, getString(R.string.chooser_header)));
    }

    public boolean validateEmptyInput() {
        if (!isEmailValid(mailEditText.getText().toString())) {
            Toast.makeText(MainActivity.this, R.string.mail_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (topicEditText.getText().toString().trim().equals("") ||
                messageEditText.getText().toString().trim().equals("")) {
            Toast.makeText(MainActivity.this, R.string.empty_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isEmailValid(CharSequence inputStr) {
        boolean isValid = false;
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(inputStr).matches()) {
            isValid = true;
        }
        return isValid;
    }
}
