package cordproject.lol.papercraft;

import android.app.Application;

import java.util.HashMap;

import cordproject.lol.papercraft.controller.ControllerP;
import cordproject.lol.papercraft.controller.SystemControllerP;

public class PaperCraftApplicationP extends Application {

    private static HashMap<Integer, ControllerP> controllerHashMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        SystemControllerP systemController = new SystemControllerP();
        controllerHashMap.put(ControllerP.SYSTEM_CONTROLLER, systemController);

    }

    public static ControllerP getController(int controller) {
        return controllerHashMap.get(controller);
    }
}
