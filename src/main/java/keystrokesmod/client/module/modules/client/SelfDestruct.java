package keystrokesmod.client.module.modules.client;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;

public class SelfDestruct extends Module {
    
    public static boolean selfDestructed;
    
    public SelfDestruct() {
        super("Self Destruct", ModuleCategory.client);
    }

    public void onEnable() {
        this.disable();
        selfDestructed = true;
        mc.displayGuiScreen(null);

        for (Module module : Raven.moduleManager.getModules()) {
            module.unRegister();
        }
    }
}
