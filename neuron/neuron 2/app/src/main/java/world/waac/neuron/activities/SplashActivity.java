package world.waac.neuron.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.waac.neuron.R;
import world.waac.neuron.globals.GlobalConstants;
import world.waac.neuron.globals.NotificationToast;
import world.waac.neuron.globals.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SplashActivity extends AppCompatActivity {

    List<String> requestingPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_lets_go)
    public void onClickBtnLetsGo() {

        requestingPermissions.clear();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestingPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestingPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PERMISSION_GRANTED) {
            requestingPermissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (requestingPermissions.size() != 0) {
            ActivityCompat.requestPermissions(
                    this,
                    requestingPermissions.toArray(new String[requestingPermissions.size()]),
                    GlobalConstants.REQUEST_CODE_PERMISSION_GRANT);
        } else {
            goToMain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case GlobalConstants.REQUEST_CODE_PERMISSION_GRANT: {
                // If request is cancelled, the result arrays are empty.

                boolean allGranted = true;

                for (int result : grantResults) {
                    if (result == PERMISSION_DENIED) {
                        allGranted = false;
                    }
                }

                if (permissions.length != requestingPermissions.size()) {
                    allGranted = false;
                }
                if (allGranted) {
                    goToMain();
                } else {
                    NotificationToast.showToast(this, "please grant all permissions to continue.");
                }
                break;
            }
        }
    }

    private void goToMain() {

        if (Utility.isWiFiEnabled(this)) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            NotificationToast.showToast(this, "Please enable Wi-Fi to continue");
        }


    }

}
