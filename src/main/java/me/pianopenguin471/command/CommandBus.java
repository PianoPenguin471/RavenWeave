package me.pianopenguin471.command;

import lombok.experimental.UtilityClass;
import net.weavemc.api.ModInitializer;
import net.weavemc.api.event.EventBus;
import ravenweave.client.event.ChatSentEvent;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The Command Bus manages commands by Weave mods.
 */
@SuppressWarnings("unused")
@UtilityClass
public class CommandBus {

    private final List<Command> commands = new ArrayList<>();

    /**
     * Commands can be registered at any time during the client lifecycle, but if you intend
     * on normal behavior, you should register during the {@link ModInitializer#preInit(Instrumentation)} phase.
     *
     * @param command The command instance to register.
     */
    public void register(Command command) {
        commands.add(command);
    }

    static {
        EventBus.subscribe(ChatSentEvent.class, e -> {
            if (!e.getMessage().startsWith("/")) return;

            String[] split = e.getMessage().substring(1).split("\\s+");

            Iterator<Command> matching = commands.stream().filter(c -> c.matches(split[0])).iterator();
            if (!matching.hasNext()) return;

            e.setCancelled(true);
            String[] args = Arrays.copyOfRange(split, 1, split.length);
            matching.forEachRemaining(c -> c.handle(args));
        });
    }

}
