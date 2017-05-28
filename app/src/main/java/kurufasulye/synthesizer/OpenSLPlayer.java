package kurufasulye.synthesizer;

import android.content.Context;
import android.media.AudioManager;

/**
 * A <code>Player</code> object implemented using
 * native OpenSL apis
 */
public class OpenSLPlayer extends Player {
    private static final String TAG = "OpenSLPlayer";

    private Context context;

    public OpenSLPlayer(Context context) {
        this.context = context;
    }

    @Override
    public boolean start() {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        int nativeSampleRate = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));
        int nativeSampleBufferSize = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER));

        if (!init(nativeSampleRate, nativeSampleBufferSize))
            return false;

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void mute(boolean isMute) {

    }

    /**
     * Load native library
     */
    static {
        System.loadLibrary("openslplayerjni");
    }

    /**
     * Native function declarations
     */
    private native boolean init(int sampleRate, int bufferSize);
}
