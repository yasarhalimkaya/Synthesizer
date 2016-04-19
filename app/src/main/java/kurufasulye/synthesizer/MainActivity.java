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
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenToggleButton = (ToggleButton) findViewById(R.id.listenToggleButton);
        listenToggleButton.setOnClickListener(this);

        // Use AndroidPlayer
        player = new AndroidPlayer();
    }

    @Override
    protected void onDestroy() {
        player.stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listenToggleButton:
                handleListenToggleButtonClick();
                break;
            default:
                if (BuildConfig.DEBUG)
                    Log.w(TAG, "Unhandled onClick event with View : " + v);
                break;
        }
    }

    private void handleListenToggleButtonClick() {
        Log.d(TAG, "onClick called");

        if (listenToggleButton.isChecked()) {
            startListening();
        } else {
            stopListening();
        }
    }

    private void startListening() {
        Log.d(TAG, "startListening");

        if (!player.start()) {
            Toast.makeText(this, "Play Error", Toast.LENGTH_SHORT).show();
            listenToggleButton.setChecked(false);
        }

        player.start();
    }

    private void stopListening() {
        Log.d(TAG, "stopListening");

        player.stop();
    }
}
