package ravenweave.client.module.modules.client;

import com.google.common.eventbus.Subscribe;
import ravenweave.client.event.impl.GameLoopEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;
import ravenweave.client.utils.version.Version;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateCheck extends Module {
    public static DescriptionSetting howToUse;
    public static TickSetting copyToClipboard;
    public static TickSetting openLink;

    private Future<?> f;
    private final ExecutorService executor;
    private final Runnable task;

    public UpdateCheck() {
        super("Update", ModuleCategory.client);

        this.registerSetting(howToUse = new DescriptionSetting(Utils.Java.capitalizeWord("command") + ": update"));
        this.registerSetting(copyToClipboard = new TickSetting("Copy to clipboard", true));
        this.registerSetting(openLink = new TickSetting("Open dl in browser", true));

        executor = Executors.newFixedThreadPool(1);
        task = () -> {
            Version latest = Raven.versionManager.getLatestVersion();
            Version current = Raven.versionManager.getClientVersion();
            if (latest.isNewerThan(current)) {
                Utils.Player.sendMessageToSelf(
                        "The current version or RavenWeave is outdated. Visit https://github.com/PianoPenguin471/RavenWeave/releases to download the latest version.");
                Utils.Player.sendMessageToSelf("https://github.com/PianoPenguin471/RavenWeave/releases");
            }

            if (current.isNewerThan(latest)) {
                Utils.Player.sendMessageToSelf("You are on a beta build of raven");
                Utils.Player.sendMessageToSelf("https://github.com/PianoPenguin471/RavenWeave/releases");
            } else {
                Utils.Player.sendMessageToSelf("You are on the latest public version!");
            }

            if (copyToClipboard.isToggled())
                if (Utils.Client.copyToClipboard(Raven.downloadLocation))
                    Utils.Player.sendMessageToSelf("Successfully copied download link to clipboard!");
            Utils.Player.sendMessageToSelf(Raven.sourceLocation);

            if (openLink.isToggled()) {
                try {
                    URL url = new URL(Raven.sourceLocation);
                    Utils.Client.openWebpage(url);
                    Utils.Client.openWebpage(new URL(Raven.downloadLocation));
                } catch (MalformedURLException bruh) {
                    bruh.printStackTrace();
                    Utils.Player
                            .sendMessageToSelf("&cFailed to open page! Please report this bug in RavenWeave's discord");
                }
            }

            this.disable();
        };
    }

    @Subscribe
    public void onGameLoop(GameLoopEvent e) {
        if (f == null) {
            f = executor.submit(task);
            Utils.Player.sendMessageToSelf("Update check started!");
        } else if (f.isDone()) {
            f = executor.submit(task);
            Utils.Player.sendMessageToSelf("Update check started!");
        }
    }
}
