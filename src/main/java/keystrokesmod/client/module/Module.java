package keystrokesmod.client.module;

import com.google.gson.JsonObject;
import keystrokesmod.client.clickgui.raven.components.BindComponent;
import keystrokesmod.client.clickgui.raven.components.ModuleComponent;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.notifications.NotificationRenderer;
import net.minecraft.client.Minecraft;
import net.weavemc.loader.api.event.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Module {
    protected static Minecraft mc;
    private final String moduleName;
    private final ModuleCategory moduleCategory;
    protected ArrayList<Setting> settings;
    protected boolean hasBind = true, showInHud = true, clientConfig, enabled;
    protected boolean defaultEnabled = enabled;
    protected int keycode;
    protected int bindmode = 1;
    protected int defualtKeyCode = keycode;
    protected ModuleComponent component;
    protected boolean registered;
    private boolean isToggled;

    public Module(String name, ModuleCategory moduleCategory) {
        this.moduleName = name;
        this.moduleCategory = moduleCategory;
        this.settings = new ArrayList<>();
        mc = Minecraft.getMinecraft();
    }

    public void guiUpdate() {

    }

    protected <E extends Module> E withKeycode(int i) {
        this.keycode = i;
        this.defualtKeyCode = i;
        return (E) this;
    }

    protected <E extends Module> E withEnabled(boolean i) {
        this.enabled = i;
        this.defaultEnabled = i;
        try {
            setToggled(i);
        } catch (Exception e) {
        }
        return (E) this;
    }

    public <E extends Module> E withDescription(String i) {
        return (E) this;
    }

    public JsonObject getConfigAsJson() {
        JsonObject settings = new JsonObject();

        for (Setting setting : this.settings)
            if (setting != null) {
                JsonObject settingData = setting.getConfigAsJson();
                settings.add(setting.settingName, settingData);
            }

        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        if (hasBind)
            if (this.component.bindModeSetting.getMode() == BindComponent.EventType.Toggle) {
                this.bindmode = 1;
            }
        if (this.component.bindModeSetting.getMode() == BindComponent.EventType.Hold) {
            this.bindmode = 2;
        }
        data.addProperty("keycode", keycode);
        data.addProperty("bindmode", bindmode);
        data.addProperty("showInHud", showInHud);
        data.add("settings", settings);

        return data;
    }

    public void applyConfigFromJson(JsonObject data) {
        try {
            if (hasBind)
                this.keycode = data.get("keycode").getAsInt();
            this.bindmode = data.get("bindmode").getAsInt();
            if (this.bindmode == 1) {
                this.component.bindModeSetting.setMode(BindComponent.EventType.Toggle);
            }
            if (this.bindmode == 2) {
                this.component.bindModeSetting.setMode(BindComponent.EventType.Hold);
            }

            setToggled(data.get("enabled").getAsBoolean());
            JsonObject settingsData = data.get("settings").getAsJsonObject();
            for (Setting setting : getSettings())
                if (settingsData.has(setting.getName()))
                    setting.applyConfigFromJson(settingsData.get(setting.getName()).getAsJsonObject());
            this.showInHud = data.get("showInHud").getAsBoolean();
        } catch (NullPointerException ignored) {
        }
        postApplyConfig();
    }

    public void postApplyConfig() {

    }

    public void keybind() {
        if ((this.keycode != 0) && this.canBeEnabled()) {
            if (this.component.bindModeSetting.getMode() == BindComponent.EventType.Hold) {
                // For 'Hold' mode
                if (Keyboard.isKeyDown(this.keycode)) {
                    this.enable();
                } else {
                    this.disable();
                }
            } else {
                // For 'Toggle' mode
                if (!this.isToggled && Keyboard.isKeyDown(this.keycode)) {
                    this.toggle();
                    this.isToggled = true;
                } else if (!Keyboard.isKeyDown(this.keycode)) {
                    this.isToggled = false;
                }
            }
        }
    }


    public boolean canBeEnabled() {
        return true;
    }

    public boolean showInHud() {
        return showInHud;
    }

    public void enable() {
        if (!canBeEnabled())
            return;
        this.enabled = true;
        this.onEnable();
        if (enabled && !registered) {
            Raven.eventBus.register(this);
            registered = true;
            EventBus.subscribe(this);
        }
        NotificationRenderer.moduleStateChanged(this);
    }

    public void disable() {
        if (!canBeEnabled())
            return;
        this.enabled = false;
        if (registered) {
            Raven.eventBus.unregister(this);
            EventBus.unsubscribe(this);
            registered = false;
        }
        this.onDisable();
        NotificationRenderer.moduleStateChanged(this);

    }


    public void setToggled(boolean enabled) {
        if (!canBeEnabled())
            return;
        if (enabled)
            enable();
        else
            disable();
    }

    public boolean isBindable() {
        return hasBind;
    }

    public String getName() {
        return this.moduleName;
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings)
            if (setting.getName().equalsIgnoreCase(name))
                return setting;
        return null;
    }

    public void registerSetting(Setting Setting) {
        this.settings.add(Setting);
    }

    public void registerSettings(Setting... settings) {
        Collections.addAll(this.settings, settings);
    }

    public void setVisibleInHud(boolean vis) {
        this.showInHud = vis;
    }

    public ModuleCategory moduleCategory() {
        return this.moduleCategory;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        if (this.enabled)
            this.disable();
        else
            this.enable();
    }

    public void guiButtonToggled(Setting b) {

    }

    public int getKeycode() {
        return this.keycode;
    }

    public void setBind(int keybind) {
        this.keycode = keybind;
    }

    public void resetToDefaults() {
        this.keycode = defualtKeyCode;
        this.setToggled(defaultEnabled);

        for (Setting setting : this.settings)
            setting.resetToDefaults();
    }

    public void setModuleComponent(ModuleComponent component) {
        this.component = component;
    }

    public void onGuiClose() {

    }

    public String getBindAsString() {
        return keycode == 0 ? "None" : Keyboard.getKeyName(keycode);
    }


    public boolean isClientConfig() {
        return clientConfig;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void unRegister() {
        if (registered) {
            registered = false;
            Raven.eventBus.unregister(this);
            onDisable();
        }
    }

    public enum ModuleCategory {
        category(true, null, "Raven B++"),
        combat(false, category, "Combat"),
        movement(false, category, "Movement"),
        player(false, category, "Player"),
        world(false, category, "World"),
        render(false, category, "Render"),
        minigames(false, category, "Minigames"),
        other(false, category, "Other"),
        client(false, category, "Client"),
        hotkey(false, category, "Hotkey"),
        config(false, client, "Config"),
        beta(false, category, "BETA");

        private final boolean defaultShown;
        private final ModuleCategory topCategory;
        private final String name;
        private final List<ModuleCategory> childCategories = new ArrayList<ModuleCategory>();

        ModuleCategory(boolean defaultShown, ModuleCategory topCategory, String name) {
            if (topCategory != null)
                topCategory.addChildCategory(this);
            this.defaultShown = defaultShown;
            this.topCategory = topCategory;
            this.name = name;
        }

        public void addChildCategory(ModuleCategory moduleCategory) {
            childCategories.add(moduleCategory);
        }

        public List<ModuleCategory> getChildCategories() {
            return childCategories;
        }

        public String getName() {
            return name;
        }

        public boolean isShownByDefault() {
            return defaultShown;
        }

        public ModuleCategory getParentCategory() {
            return topCategory;
        }
    }
}
