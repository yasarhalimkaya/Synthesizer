package kurufasulye.synthesizer;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * A <code>Player</code> implementation
 * using Android's <code>AudioRecord</code> and <code>AudioTrack</code> apis
 */
public class AndroidPlayer extends Player implements Runnable {
    private static final String TAG = "AndroidPlayer";

    private Context context;

    private boolean playing = false;

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    private int SAMPLE_RATE; // Hz
    private int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private int BUFFER_SIZE;
    private short[] audioBuffer;

    private Thread thread;

    public AndroidPlayer(Context context) {
        this.context = context;
    }

    @Override
    public boolean start() {
        if (!playing && init()) {
            audioRecord.startRecording();
            audioTrack.play();

            audioBuffer = new short[BUFFER_SIZE];

            if (thread == null)
                thread = new Thread(this);

            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();

            playing = true;
        }

        return playing;
    }

    @Override
    public void run() {
        // TODO : Should try/catch exceptions?
        while (!thread.isInterrupted()) {
            int readDataSize = audioRecord.read(audioBuffer, 0, BUFFER_SIZE);
            audioTrack.write(audioBuffer, 0, readDataSize);
        }

        thread = null;
    }

    private boolean init() {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        SAMPLE_RATE = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));

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

        BUFFER_SIZE = minRecordBufferSize > minTrackBufferSize ? minRecordBufferSize : minTrackBufferSize;

        return true;
    }

    @Override
    public void stop() {
        if (playing) {
            if (thread != null)
                thread.interrupt();

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;

            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;

            playing = false;
        }
    }

    @Override
    public void mute(boolean isMute) {
        if (!playing) {
            return;
        }

        if (isMute) {
            audioTrack.pause();
        } else {
            audioTrack.play();
        }
    }
}
