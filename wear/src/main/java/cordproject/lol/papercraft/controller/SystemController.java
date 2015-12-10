package cordproject.lol.papercraft.controller;

/**
 * Created by matthewlim on 10/31/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class SystemController extends Controller{


    private boolean soundPreferenceOn = true;

    public void notifyOnQuitRequested() {
        for (ControllerListener listener : listenerMap) {
            ((SystemControllerListener)listener).onQuitRequested();
        }
    }

    public void notifyOnMusicStopRequested() {
        for (ControllerListener listener : listenerMap) {
            ((SystemControllerListener)listener).onMusicStopRequested();
        }
    }

    public void notifyOnMusicStartRequested() {
        for (ControllerListener listener : listenerMap) {
            ((SystemControllerListener)listener).onMusicStartRequested();
        }
    }

    public void notifyOnExplosionSoundRequested() {
        for (ControllerListener listener : listenerMap) {
            ((SystemControllerListener)listener).onExplosionSoundRequested();
        }
    }

    public void setSoundPreferenceOn(boolean soundPreferenceOn) {
        this.soundPreferenceOn = soundPreferenceOn;
    }

    public boolean isSoundPreferenceOn() {
        return soundPreferenceOn;
    }

    public static abstract class SystemControllerListener extends ControllerListener{
        public void onQuitRequested(){}
        public void onMusicStopRequested(){}
        public void onMusicStartRequested(){}
        public void onExplosionSoundRequested(){}
    }

}
