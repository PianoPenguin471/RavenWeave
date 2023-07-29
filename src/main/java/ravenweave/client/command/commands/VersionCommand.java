package ravenweave.client.command.commands;

import ravenweave.client.clickgui.raven.Terminal;
import ravenweave.client.command.Command;
import ravenweave.client.main.Raven;
import ravenweave.client.utils.version.Version;

public class VersionCommand extends Command {
    public VersionCommand() {
        super("version", "tells you what build of B++ you are using", 0, 0, new String[] {},
                new String[] { "v", "ver", "which", "build", "b" });
    }

    @Override
    public void onCall(String[] args) {
        Version clientVersion = Raven.versionManager.getClientVersion();
        Version latestVersion = Raven.versionManager.getLatestVersion();

        Terminal.print("Your build: " + clientVersion);
        Terminal.print("Latest version: " + latestVersion);

    }
}
