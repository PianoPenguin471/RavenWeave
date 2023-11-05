package ravenweave.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.weavemc.loader.api.event.EventBus;
import ravenweave.client.config.ClientConfig;
import ravenweave.client.clickgui.kv.KvCompactGui;
import ravenweave.client.clickgui.raven.ClickGui;
import ravenweave.client.command.CommandManager;
import ravenweave.client.config.ConfigManager;
import ravenweave.client.module.ModuleManager;
import ravenweave.client.utils.MouseManager;
import ravenweave.client.utils.PingChecker;
import ravenweave.client.utils.RenderUtils;
import ravenweave.client.utils.font.FontUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Raven {

	public static boolean debugger;
    public static CommandManager commandManager;
    public static ConfigManager configManager;
    public static ClientConfig clientConfig;

    public static final ModuleManager moduleManager = new ModuleManager();

    public static ClickGui clickGui;
    public static KvCompactGui kvCompactGui;

    private static final ScheduledExecutorService ex = Executors.newScheduledThreadPool(2);

    public static ResourceLocation mResourceLocation;

    public static final String osName, osArch;
    public static final List<Object> registered = new ArrayList<>();
    public static final Minecraft mc = Minecraft.getMinecraft();

    static {
        osName = System.getProperty("os.name").toLowerCase();
        osArch = System.getProperty("os.arch").toLowerCase();
    }

    public static void init() {
        register(new MouseManager());
        register(new PingChecker());

        FontUtil.bootstrap();

        Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));

        mResourceLocation = RenderUtils.getResourcePath("/assets/ravenweave/raven.png");

        commandManager = new CommandManager();
        clickGui = new ClickGui();
        kvCompactGui = new KvCompactGui();
        configManager = new ConfigManager();
        clientConfig = new ClientConfig();
        clientConfig.applyConfig();
    }

    public static void register(Object obj) {
        registered.add(obj);
        EventBus.subscribe(obj);
    }

    public static ScheduledExecutorService getExecutor() {
        return ex;
    }
}