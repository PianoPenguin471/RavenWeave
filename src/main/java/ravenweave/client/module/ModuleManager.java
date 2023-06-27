package ravenweave.client.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ravenweave.client.main.Raven;
import ravenweave.client.module.Module.ModuleCategory;
import ravenweave.client.module.modules.HUD;
import ravenweave.client.module.modules.client.*;
import ravenweave.client.module.modules.combat.*;
import ravenweave.client.module.modules.config.*;
import ravenweave.client.module.modules.hotkey.*;
import ravenweave.client.module.modules.minigames.*;
import ravenweave.client.module.modules.movement.*;
import ravenweave.client.module.modules.other.*;
import ravenweave.client.module.modules.player.*;
import ravenweave.client.module.modules.render.*;
import ravenweave.client.module.modules.world.*;
import ravenweave.client.utils.Utils;
import net.minecraft.client.gui.FontRenderer;

public class ModuleManager {
    private List<Module> modules = new ArrayList<>();

    public static boolean initialized;
    public GuiModuleManager guiModuleManager;

    public ModuleManager() {
        System.out.println(ModuleCategory.values());
        if(initialized)
            return;
        this.guiModuleManager = new GuiModuleManager();
        addModule(new ChestStealer());
        addModule(new AutoArmour());
        addModule(new LeftClicker());
        addModule(new ClickAssist());
        addModule(new RightClicker());
        addModule(new AimAssist());
        addModule(new DelayRemover());
        addModule(new HitBoxes());
        addModule(new Reach());
        addModule(new Velocity());
        addModule(new Boost());
        addModule(new Fly());
        addModule(new InvMove());
        addModule(new KeepSprint());
        addModule(new NoSlow());
        addModule(new Sprint());
        addModule(new StopMotion());
        addModule(new LegitSpeed());
        addModule(new Timer());
        addModule(new VClip());
        addModule(new HClip());
        addModule(new AutoPlace());
        addModule(new BedAura());
        addModule(new FallSpeed());
        addModule(new FastPlace());
        addModule(new Freecam());
        addModule(new NoFall());
        addModule(new SafeWalk());
        addModule(new AntiBot());
        addModule(new Chams());
        addModule(new ChestESP());
        addModule(new Nametags());
        addModule(new PlayerESP());
        addModule(new Tracers());
        addModule(new HUD());
        addModule(new BridgeInfo());
        addModule(new DuelsStats());
        addModule(new MurderMystery());
        addModule(new SumoFences());
        addModule(new SlyPort());
        addModule(new FakeChat());
        addModule(new WaterBucket());
        addModule(new Terminal());
        addModule(new ClickGuiModule());
        addModule(new ChatLogger());
        addModule(new BridgeAssist());
        addModule(new UpdateCheck());
        addModule(new AutoHeader());
        addModule(new Blocks());
        addModule(new Ladders());
        addModule(new Weapon());
        addModule(new Pearl());
        addModule(new Armour());
        addModule(new Healing());
        addModule(new Trajectories());
        addModule(new WTap());
        addModule(new BlockHit());
        addModule(new STap());
        addModule(new AutoWeapon());
        addModule(new BedwarsOverlay());
        addModule(new ShiftTap());
        addModule(new AutoBlock());
        addModule(new MiddleClick());
        addModule(new Projectiles());
        addModule(new FakeHud());
        addModule(new ConfigSettings());
        addModule(new Parkour());
        addModule(new JumpReset());
        addModule(new KillAura());
        addModule(new Spin());
        addModule(new AutoGHead());
        addModule(new Radar());
        addModule(new Scaffold());
        addModule(new Blink());
        addModule(new AutoSoup());
        addModule(new Targets());
        addModule(new SpeedTest());
        addModule(new TargetHUD());
        addModule(new LongJump());
        initialized = true;
    }

    public void addModule(Module m) {
        modules.add(m);
    }

    public void removeModuleByName(String s) {
        Module m = getModuleByName(s);
        modules.remove(m);
    }

    // prefer using getModuleByClazz();
    // ok might add in 1.0.18
    public Module getModuleByName(String name) {
        if (!initialized)
            return null;

        for (Module module : modules)
			if (module.getName().replaceAll(" ", "").equalsIgnoreCase(name) || module.getName().equalsIgnoreCase(name))
                return module;
        return null;
    }

    public Module getModuleByClazz(Class<? extends Module> c) {
        if (!initialized)
            return null;

        for (Module module : modules)
			if (module.getClass().equals(c))
                return module;
        return null;
    }

    public List<Module> getModules() {
        ArrayList<Module> allModules = new ArrayList<>(modules);
        try {
            allModules.addAll(Raven.configManager.configModuleManager.getConfigModules());
        } catch (NullPointerException ignored) {
        }
        try {
            allModules.addAll(guiModuleManager.getModules());
        } catch (NullPointerException ignored) {
        }
        return allModules;
    }

    public List<Module> getConfigModules() {
        List<Module> modulesOfC = new ArrayList<>();

        for (Module mod : getModules())
			if (!mod.isClientConfig())
				modulesOfC.add(mod);

        return modulesOfC;
    }

    public List<Module> getClientConfigModules() {
        List<Module> modulesOfCC = new ArrayList<>();

        for (Module mod : getModules())
			if (mod.isClientConfig())
				modulesOfCC.add(mod);

        return modulesOfCC;
    }

    public List<Module> getModulesInCategory(Module.ModuleCategory categ) {
        ArrayList<Module> modulesOfCat = new ArrayList<>();

        for (Module mod : getModules())
			if (mod.moduleCategory().equals(categ))
				modulesOfCat.add(mod);

        return modulesOfCat;
    }

    public void sort() {
        modules.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2.getName())
                - Utils.mc.fontRendererObj.getStringWidth(o1.getName()));
    }

    public int numberOfModules() {
        return modules.size();
    }

    public void sortLongShort() {
        modules.sort(Comparator.comparingInt(o2 -> Utils.mc.fontRendererObj.getStringWidth(o2.getName())));
    }

    public void sortShortLong() {
        modules.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2.getName())
                - Utils.mc.fontRendererObj.getStringWidth(o1.getName()));
    }

    public int getLongestActiveModule(FontRenderer fr) {
        int length = 0;
        for (Module mod : modules)
			if (mod.isEnabled())
				if (fr.getStringWidth(mod.getName()) > length)
					length = fr.getStringWidth(mod.getName());
        return length;
    }

    public int getBoxHeight(FontRenderer fr, int margin) {
        int length = 0;
        for (Module mod : modules)
			if (mod.isEnabled())
				length += fr.FONT_HEIGHT + margin;
        return length;
    }

}
