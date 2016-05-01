package kurufasulye.synthesizer;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.util.Log;

/**
 * A <code>Player</code> object implemented using
 * native OpenSL apis
 */
public class OpenSLPlayer implements Player {
    private static final String TAG = "OpenSLPlayer";

    private Context context;
    private String nativeSampleRate;
    private String nativeSampleBufferSize;

    public OpenSLPlayer(Context context) {
        this.context = context;
    }

    @Override
    public boolean start() {
        if (!isSystemSupportRecording()) {
            Log.e(TAG, "System does not support recording");
            return false;
        }

        return init();
    }

    @Override
    public void stop() {

    }

    @Override
    public void mute(boolean isMute) {

    }

    private boolean isSystemSupportRecording() {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        nativeSampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        nativeSampleBufferSize = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);

        int recordBufferSize = AudioRecord.getMinBufferSize(
                Integer.parseInt(nativeSampleRate),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (recordBufferSize == AudioRecord.ERROR || recordBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return false;
        }

        return true;
    }

    /**
     * Load native library
     */
    static {
        System.loadLibrary("openslplayerjni");
    }

    /**
     * @return <code>true</code> on success
     *         <code>false</code> otherwise
     */
    private native boolean init();
}
