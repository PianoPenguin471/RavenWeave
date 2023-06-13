package keystrokesmod.client.command.commands;

import keystrokesmod.client.command.Command;

import static keystrokesmod.client.clickgui.raven.Terminal.print;
import static keystrokesmod.client.utils.Utils.mc;


import java.util.ArrayList;

import keystrokesmod.client.module.modules.client.Targets;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;


public class Friends extends Command {
    public static ArrayList<Entity> friends = new ArrayList<>();
    public Friends() {
        super("friends", "Allows you to manage and view your friends list", 1, 2,
                new String[] { "add / remove / list", "Player's name" }, new String[] { "f", "amigos", "lonely4ever" });
    }

    @Override
    public void onCall(String[] args) {
        if (args.length == 0) {
            listFriends();
        } else if (args[0].equalsIgnoreCase("list")) {
            listFriends();
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                boolean added = addFriend(args[1]);
                if (added) {
                    print("Successfully added " + args[1] + " to your friends list!");
                } else {
                    print("An error occurred!");
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                boolean removed = removeFriend(args[1]);
                if (removed) {
                    print("Successfully removed " + args[1] + " from your friends list!");
                } else {
                    print("An error occurred!");
                }
            }
        } else {
            this.incorrectArgs();
        }
    }

    public static ArrayList<Entity> getFriends() {
        return friends;
    }

    public void listFriends() {
        if (getFriends().isEmpty()) {
            print("You have no friends. :(");
        } else {
            print("Your friends are:");
            for (Entity entity : getFriends()) {
                print(entity.getName());
            }
        }
    }

    public static Entity getEnemy() {
        return Targets.getTarget();
    }

    public static void addFriend(Entity entityPlayer) {
        friends.add(entityPlayer);
    }

    public static boolean addFriend(String name) {
        boolean found = false;
        for (Entity entity : mc.theWorld.getLoadedEntityList())
            if (entity.getName().equalsIgnoreCase(name) || entity.getCustomNameTag().equalsIgnoreCase(name))
                if (!Targets.isAFriend(entity)) {
                    addFriend(entity);
                    found = true;
                }

        return found;
    }

    public static boolean removeFriend(String name) {
        boolean removed = false;
        boolean found = false;
        for (NetworkPlayerInfo networkPlayerInfo : new ArrayList<>(mc.getNetHandler().getPlayerInfoMap())) {
            Entity entity = mc.theWorld.getPlayerEntityByName(networkPlayerInfo.getDisplayName().getUnformattedText());
            if (entity.getName().equalsIgnoreCase(name) || entity.getCustomNameTag().equalsIgnoreCase(name)) {
                removed = removeFriend(entity);
                found = true;
            }
        }

        return found && removed;
    }

    public static boolean removeFriend(Entity entityPlayer) {
        try {
            friends.remove(entityPlayer);
        } catch (Exception eeeeee) {
            eeeeee.printStackTrace();
            return false;
        }
        return true;
    }
    
}
