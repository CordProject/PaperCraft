package cordproject.lol.papercraft;

import android.app.Application;

import java.util.HashMap;

import cordproject.lol.papercraft.controller.Controller;
import cordproject.lol.papercraft.controller.GameController;
import cordproject.lol.papercraft.controller.SystemController;

/**
 * Created by matthewlim on 10/31/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class PaperCraftApplication extends Application {

    private static HashMap<Integer, Controller> controllerHashMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        SystemController systemController = new SystemController();
        GameController gameController = new GameController();
        controllerHashMap.put(Controller.SYSTEM_CONTROLLER, systemController);
        controllerHashMap.put(Controller.GAME_CONTROLLER, gameController);

    }

    public static Controller getController(int controller) {
        return controllerHashMap.get(controller);
    }
}
