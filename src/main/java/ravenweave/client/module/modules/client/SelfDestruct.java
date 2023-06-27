package ravenweave.client.module.modules.client;

import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;

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
