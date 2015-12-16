package cordproject.lol.papercraft.controller;

import java.util.ArrayList;


public abstract class Controller {

    public static int SYSTEM_CONTROLLER = 0;
    public static int GAME_CONTROLLER = 1;

    protected ArrayList<ControllerListener> listenerMap = new ArrayList<>();

    public void addListener(ControllerListener listener) {
        listenerMap.add(listener);
    }

    public void removeListener(ControllerListener listener) {
        listenerMap.remove(listener);
    }

    public static abstract class ControllerListener {

    }
}
