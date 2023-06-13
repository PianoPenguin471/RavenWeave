package keystrokesmod.client.module.modules.client;

import java.util.Comparator;
import java.util.List;

import keystrokesmod.client.command.commands.Friends;
import net.weavemc.loader.api.event.SubscribeEvent;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.combat.KillAura;
import keystrokesmod.client.module.modules.world.AntiBot;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import me.PianoPenguin471.events.AttackEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Targets extends Module {

    private static TickSetting friends, teams, invis, bots, naked, debug;
    private static SliderSetting fov, distance, lockDist,auraFov;
    private static ComboSetting<SortMode> sortMode;

    public static EntityPlayer lockedTarget;

    public Targets() {
        super("Targets", ModuleCategory.client);
        this.registerSettings(
                        friends = new TickSetting("Target friends", false),
                        teams = new TickSetting("Target teammates", false),
                        invis = new TickSetting("Target invis", false),
                        bots = new TickSetting("Target bots", false),
                        naked = new TickSetting("Target naked", false),
                        fov = new SliderSetting("General Fov", 120, 0, 360, 1),
                        auraFov = new SliderSetting("Aura Fov", 360, 0, 360, 1),
                        distance = new SliderSetting("Distance", 3.5, 0, 10, 0.1),
                        sortMode = new ComboSetting("Sort mode", SortMode.Distance),
                        lockDist = new SliderSetting("Lock distance", 4, 0, 10, 0.1),
                        debug = new TickSetting("debug", false)
                        );
        onEnable();
    }

    @Override
    public boolean canBeEnabled() {
        return false;
    }

    private static double getFOV() {
        return Raven.moduleManager.getModuleByClazz(KillAura.class).isEnabled() ? auraFov.getInput() : fov.getInput();
    }
    
    @Override
    public void postApplyConfig() {
        guiButtonToggled(sortMode);
    }

    @Override
    public void guiButtonToggled(Setting b) {
        if(b == sortMode) {
            lockDist.hideComponent(sortMode.getMode() == SortMode.Lock);
        }
    }

    @SubscribeEvent
    public void onForgeEvent(AttackEntityEvent e) {
        lockedTarget = e.target instanceof EntityPlayer ? (EntityPlayer) e.target : lockedTarget;
    }

    public static EntityPlayer getTarget() {
        List<EntityPlayer> en = Utils.Player.getClosePlayers(distance.getInput());
        if (en == null) return null;
        en.removeIf(player -> !isValidTarget(player));
        if(debug.isToggled()) en.forEach(target -> Utils.Player.sendMessageToSelf(sortMode.getMode().sv.value(target) + " " ));
        return en.isEmpty() ? null : en.stream().min(Comparator.comparingDouble(target -> sortMode.getMode().sv.value(target))).get();
    }

    public static boolean isValidTarget(EntityPlayer ep) {
        return (
                (ep != mc.thePlayer)
                && (bots.isToggled() || !AntiBot.bot(ep))
                && (friends.isToggled() || !isAFriend(ep))
                && (teams.isToggled() || !isATeamMate(ep))
                && (invis.isToggled() || !ep.isInvisible())
                && (naked.isToggled() || !Utils.Player.isPlayerNaked(ep))
                && Utils.Player.fov(ep, (float) getFOV())
                );
    }

    public static boolean isAFriend(Entity entity) {
        if (entity == mc.thePlayer)
            return true;

        for (Entity wut : Friends.friends)
            if (wut.equals(entity))
                return true;
        return false;
    }

    public static boolean isATeamMate(Entity entity) {
        try {
            EntityPlayer bruhentity = (EntityPlayer) entity;
            if (Raven.debugger) {
                Utils.Player.sendMessageToSelf(
                        "unformatted / " + bruhentity.getDisplayName().getUnformattedText().replace("§", "%"));

                Utils.Player.sendMessageToSelf(
                        "susbstring entity / " + bruhentity.getDisplayName().getUnformattedText().substring(0, 2));
                Utils.Player.sendMessageToSelf(
                        "substring player / " + mc.thePlayer.getDisplayName().getUnformattedText().substring(0, 2));
            }
            if (mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName()
                    .getUnformattedText().startsWith(bruhentity.getDisplayName().getUnformattedText().substring(0, 2)))
                return true;
        } catch (Exception fhwhfhwe) {
            if (Raven.debugger)
                Utils.Player.sendMessageToSelf(fhwhfhwe.getMessage());
        }
        return false;
    }


    public enum SortMode {
        Distance(player -> mc.thePlayer.getDistanceToEntity(player)),
        Hurttime(player -> (float) player.hurtTime),
        Fov(player -> Math.abs(Utils.Player.fovFromEntityf(player))),
        Lock(player -> player == lockedTarget ? 0f : 1f);

        private final SortValue sv;

        SortMode(SortValue sv) {
            this.sv = sv;
        }

        public SortValue getSortValue() {
            return sv;
        }
    }

    @FunctionalInterface
    private interface SortValue {
        Float value(EntityPlayer player);
    }





}
