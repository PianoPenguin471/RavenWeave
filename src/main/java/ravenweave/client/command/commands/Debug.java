package ravenweave.client.command.commands;

import ravenweave.client.Raven;
import ravenweave.client.clickgui.raven.Terminal;
import ravenweave.client.command.Command;

public class Debug extends Command {
    public Debug() {
        super("debug", "Toggles B++ debbugger", 0, 0, new String[] {}, new String[] { "dbg", "log" });
    }

    @Override
    public void onCall(String[] args) {
        Raven.debugger = !Raven.debugger;
        Terminal.print((Raven.debugger ? "Enabled" : "Disabled") + " debugging.");
    }
}
