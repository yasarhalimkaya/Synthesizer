package kurufasulye.synthesizer;

/**
 * A <code>Player</code> object implemented using
 * native OpenSL apis
 */
public class OpenSLPlayer implements Player {

    static {
        System.loadLibrary("openslplayerjni");
    }

    @Override
    public boolean start() {
        return init();
    }

    @Override
    public void stop() {

    }

    @Override
    public void mute(boolean isMute) {

    }

    /**
     * @return <code>true</code> on success
     *         <code>false</code> otherwise
     */
    private native boolean init();
}
