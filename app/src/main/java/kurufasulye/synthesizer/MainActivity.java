package kurufasulye.synthesizer;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.Manifest;

public class MainActivity extends AppCompatActivity
                          implements Button.OnClickListener
{
    private final static String TAG = "MainActivity";
    private ToggleButton listenToggleButton;
    private ToggleButton muteToggleButton;
    private Player player;

    // Request codes for permissions
    private final int RECORD_AUDIO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenToggleButton = (ToggleButton) findViewById(R.id.listenToggleButton);
        listenToggleButton.setOnClickListener(this);

        muteToggleButton = (ToggleButton) findViewById(R.id.muteToggleButton);
        muteToggleButton.setOnClickListener(this);

        // Instantiate a Player
        player = new AndroidPlayer(this);
        //player = new OpenSLPlayer(this);

        // Ask for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        RECORD_AUDIO_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_REQUEST_CODE: {
                if (grantResults.length < 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    listenToggleButton.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopListening();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listenToggleButton:
                handleListenToggleButtonClick();
                break;
            case R.id.muteToggleButton:
                handleMuteToggleButtonClick();
                break;
            default:
                if (BuildConfig.DEBUG)
                    Log.w(TAG, "Unhandled onClick event with View : " + v);
                break;
        }
    }

    private void handleListenToggleButtonClick() {
        if (listenToggleButton.isChecked()) {
            startListening();
            muteToggleButton.setEnabled(true);
        } else {
            stopListening();
            muteToggleButton.setChecked(false);
            muteToggleButton.setEnabled(false);
        }
    }

    private void handleMuteToggleButtonClick() {
        if (muteToggleButton.isChecked()) {
            player.mute(true);
        } else {
            player.mute(false);
        }
    }

    private void startListening() {
        if (!player.start()) {
            Toast.makeText(this, "Play Error", Toast.LENGTH_SHORT).show();
            listenToggleButton.setChecked(false);
        }
    }

    private void stopListening() {
        player.stop();
    }
}
