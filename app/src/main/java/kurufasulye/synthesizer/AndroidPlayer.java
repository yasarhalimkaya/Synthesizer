package kurufasulye.synthesizer;

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
public class AndroidPlayer implements Player {
    private static final String TAG = "AndroidPlayer";

    private boolean mIsPlaying = false;

    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    private int SAMPLE_RATE = 44100; // Hz
    private int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    // Only responsible for starting/pausing the thread
    private PlaybackThread mPlaybackThread;

    @Override
    public boolean start() {
        if (!mIsPlaying && initResources()) {
            mAudioRecord.startRecording();
            mAudioTrack.play();
            mPlaybackThread.play(mAudioRecord, mAudioTrack);

            mIsPlaying = true;
        }

        return mIsPlaying;
    }

    private boolean initResources() {
        int minRecordBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_CONFIG, ENCODING);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_IN_CONFIG,
                ENCODING,
                minRecordBufferSize);

        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord is not initialized, returning");
            if (mAudioRecord != null) {
                mAudioRecord.release();
                mAudioRecord = null;
            }
            return false;
        }

        int minTrackBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT_CONFIG, ENCODING);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                CHANNEL_OUT_CONFIG,
                ENCODING,
                minTrackBufferSize,
                AudioTrack.MODE_STREAM);

        if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e(TAG, "AudioTrack is not initialized, returning");
            if (mAudioTrack != null) {
                mAudioTrack.release();
                mAudioTrack = null;
            }
            return false;
        }

        if (mPlaybackThread == null) {
            mPlaybackThread = new PlaybackThread();
        }

        return true;
    }

    @Override
    public void stop() {
        if (mIsPlaying) {
            mPlaybackThread.stop();

            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;

            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;

            mIsPlaying = false;
        }
    }

    @Override
    public void mute(boolean isMute) {
        Log.w(TAG, "Not implemented Player::mute()");
        return;
    }
}
