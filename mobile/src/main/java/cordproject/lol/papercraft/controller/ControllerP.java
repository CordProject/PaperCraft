package cordproject.lol.papercraft.controller;

import java.util.ArrayList;

/**
 * Created by matthewlim on 10/31/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
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
