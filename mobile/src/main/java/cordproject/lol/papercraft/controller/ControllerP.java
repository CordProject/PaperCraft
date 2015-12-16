package cordproject.lol.papercraft.controller;

import java.util.ArrayList;


public abstract class ControllerP {

    public static int SYSTEM_CONTROLLER = 0;

    protected ArrayList<ControllerListenerP> listenerMap = new ArrayList<>();

    public void addListener(ControllerListenerP listener) {
        listenerMap.add(listener);
    }

    public void removeListener(ControllerListenerP listener) {
        listenerMap.remove(listener);
    }

    public static abstract class ControllerListenerP {

    }
}
