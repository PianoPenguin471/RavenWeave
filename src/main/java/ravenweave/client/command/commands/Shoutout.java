package ravenweave.client.command.commands;

import ravenweave.client.command.Command;

import static ravenweave.client.clickgui.raven.Terminal.print;

public class Shoutout extends Command {
    public Shoutout() {
        super("shoutout", "Everyone who helped make RavenWeave", 0, 0, new String[] {}, new String[] { "love", "thanks" });
    }

    @Override
    public void onCall(String[] args) {
        print("Everyone who made RavenWeave possible:");
        print("- PianoPenguin471 (main dev)");
        print("- Syz (side dev)");
        print("- KV (b++ dev)");
        print("- Kopamed (b+ dev)");
        print("- Blowsy (b3 dev)");
        print("- Jmraich (client dev)");
        print("- Mood (java help)");
        print("- JC (b3 b2 beta tester)");
    }
}
