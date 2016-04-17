package kurufasulye.synthesizer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
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
    private boolean initialized = false;
    private ToggleButton listenToggleButton;

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private int SAMPLE_RATE = 44100; // Hz
    private int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    // Only responsible for starting/pausing the thread
    private PlaybackThread playbackThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenToggleButton = (ToggleButton) findViewById(R.id.listenToggleButton);
        listenToggleButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (initialized) {
            unInitializePlaybackResources();
            listenToggleButton.setChecked(false);
            initialized = false;
        }
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

    private boolean initializePlaybackResources() {
        int minRecordBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_CONFIG, ENCODING);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_IN_CONFIG,
                ENCODING,
                minRecordBufferSize);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord is not initialized, returning");
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            return false;
        }

        int minTrackBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT_CONFIG, ENCODING);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                    SAMPLE_RATE,
                                    CHANNEL_OUT_CONFIG,
                                    ENCODING,
                                    minTrackBufferSize,
                                    AudioTrack.MODE_STREAM);

        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e(TAG, "AudioTrack is not initialized, returning");
            if (audioTrack != null) {
                audioTrack.release();
                audioTrack = null;
            }
            return false;
        }

        if (playbackThread == null) {
            playbackThread = new PlaybackThread();
        }

        return true;
    }

    private void unInitializePlaybackResources() {
        if (playbackThread != null)
            playbackThread.pause();

        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        audioTrack.stop();
        audioTrack.release();
        audioTrack = null;
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

        initialized = initializePlaybackResources();

        if (!initialized) {
            Toast.makeText(this, "Init Error", Toast.LENGTH_SHORT).show();
            listenToggleButton.setEnabled(false);
            return;
        }

        audioRecord.startRecording();
        Log.d(TAG, "startRecording");
        audioTrack.play();
        Log.d(TAG, "play");

        playbackThread.play(audioRecord, audioTrack);
    }

    private void stopListening() {
        Log.d(TAG, "stopListening");

        if (initialized) {
            unInitializePlaybackResources();
            initialized = false;
        }
    }
}
