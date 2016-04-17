package kurufasulye.synthesizer;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

public class PlaybackThread implements Runnable {

    private final static String TAG = "PlaybackThread";
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;

    // TODO : I don't know what to give, according to what?
    private final int bufferSize = 1024;

    private Thread thread;

    public PlaybackThread(AudioRecord audioRecord, AudioTrack audioTrack) {
        this.audioRecord = audioRecord;
        this.audioTrack = audioTrack;
    }

    public void play() {
        if (thread == null)
            thread = new Thread(this);

        thread.start();
    }

    public void pause() {
        if (thread != null)
            thread.interrupt();
    }

    @Override
    public void run() {
        short[] audioData = new short[bufferSize];

        Log.d(TAG, "Thread started running");

        // TODO : Should try/catch exceptions?
        while (!thread.isInterrupted()) {
            int readDataSize = audioRecord.read(audioData, 0, bufferSize);
            audioTrack.write(audioData, 0, readDataSize);
        }

        Log.d(TAG, "Thread stopped running");

        //thread = null;
    }
}
