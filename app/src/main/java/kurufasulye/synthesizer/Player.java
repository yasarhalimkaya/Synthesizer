package kurufasulye.synthesizer;

/**
 * Player abstract class
 */
public abstract class Player {
    /**
     * Player should allocate its resources, if any,
     * and start listening and playing
     *
     * @return  <code>true</code> if the initialization is okay,
     *          and Player starts to listen and play without error
     *          <code>false</code> otherwise
     */
    public abstract boolean start();

    /**
     * Player should stop listening and playing
     * and deallocate its resources, if any,
     * as this is the only call to Player before
     * it is destroyed or any other Player tries to
     * acquire these resources
     */
    public abstract void stop();

    /**
     * Player should start/stop playback without destroying itself
     *
     * @param isMute <code>true</code> for starting playback
     *               <code>false</code> for stopping
     */
    public abstract void mute(boolean isMute);
}
