package kurufasulye.synthesizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity
                          implements Button.OnClickListener
{
    private final static String TAG = "MainActivity";
    private ToggleButton listenToggleButton;
    private ToggleButton muteToggleButton;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenToggleButton = (ToggleButton) findViewById(R.id.listenToggleButton);
        listenToggleButton.setOnClickListener(this);

        muteToggleButton = (ToggleButton) findViewById(R.id.muteToggleButton);
        muteToggleButton.setOnClickListener(this);

        // Instatiate a Player
        player = new AndroidPlayer(this);
        //player = new OpenSLPlayer(this);
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
