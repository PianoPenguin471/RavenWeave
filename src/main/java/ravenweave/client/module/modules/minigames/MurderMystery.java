package ravenweave.client.module.modules.minigames;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.weavemc.loader.api.event.RenderWorldEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import ravenweave.client.main.Raven;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.render.PlayerESP;
import ravenweave.client.module.modules.world.AntiBot;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MurderMystery extends Module {
    public static TickSetting alertMurderers;
    public static TickSetting searchDetectives;
    public static TickSetting announceMurder;
    private static final List<EntityPlayer> mur = new ArrayList();
    private static final List<EntityPlayer> det = new ArrayList();

    public MurderMystery() {
        super("Murder Mystery", ModuleCategory.minigames);
        this.registerSetting(new DescriptionSetting("Assist you in mm"));
        this.registerSetting(alertMurderers = new TickSetting("Alert", true));
        this.registerSetting(searchDetectives = new TickSetting("Search detectives", true));
        this.registerSetting(announceMurder = new TickSetting("Announce murderer", false));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldEvent event) {
        if (!this.enabled) return;
        if (Utils.Player.isPlayerInGame()) {
            PlayerESP p = (PlayerESP) Raven.moduleManager.getModuleByName("PlayerESP");
            assert p != null;
            if (p.isEnabled()) {
                p.disable();
            }

            if (!this.inMMGame()) {
                this.c();
            } else {
                Iterator<EntityPlayer> entityPlayerIterator = mc.theWorld.playerEntities.iterator();

                while (true) {
                    EntityPlayer entity;
                    do {
                        do {
                            do {
                                if (!entityPlayerIterator.hasNext()) {
                                    return;
                                }

                                entity = entityPlayerIterator.next();
                            } while (entity == mc.thePlayer);
                        } while (entity.isInvisible());
                    } while (AntiBot.bot(entity));
                    String c4 = "&7[&cALERT&7]";
                    if (entity.getHeldItem() != null && entity.getHeldItem().hasDisplayName()) {
                        Item i = entity.getHeldItem().getItem();
                        if (i instanceof ItemSword || i instanceof ItemAxe || i instanceof ItemEnderPearl
                                || i instanceof ItemHoe || i instanceof ItemPickaxe || i instanceof ItemFishingRod
                                || entity.getHeldItem().getDisplayName().contains("Knife")) {

                            if (!mur.contains(entity)) {
                                mur.add(entity);
                                String c6 = "is a murderer!";
                                if (alertMurderers.isToggled()) {
                                    String c5 = "note.pling";
                                    mc.thePlayer.playSound(c5, 1.0F, 1.0F);
                                    Utils.Player.sendMessageToSelf(c4 + " &e" + entity.getName() + " &3" + c6);
                                }

                                if (announceMurder.isToggled()) {
                                    String msg = Utils.Java.randomChoice(
                                            new String[] { entity.getName() + " " + c6, entity.getName() });
                                    mc.thePlayer.sendChatMessage(msg);
                                }
                            }
                        } else if (i instanceof ItemBow && searchDetectives.isToggled() && !det.contains(entity)) {
                            det.add(entity);
                            String c7 = "has a bow!";
                            if (alertMurderers.isToggled()) {
                                Utils.Player.sendMessageToSelf(c4 + " &e" + entity.getName() + " &3" + c7);
                            }

                            if (announceMurder.isToggled()) {
                                mc.thePlayer.sendChatMessage(entity.getName() + " " + c7);
                            }

                        }
                    }

                    int rgb = Color.cyan.getRGB();
                    if (mur.contains(entity)) {
                        rgb = Color.red.getRGB();
                    } else if (det.contains(entity)) {
                        rgb = Color.green.getRGB();
                    }

                    Utils.HUD.drawBoxAroundEntity(entity, 2, 0.0D, 0.0D, rgb, false);
                }
            }
        }
    }

    private boolean inMMGame() {
        if (Utils.Client.isHyp()) {
            if (mc.thePlayer.getWorldScoreboard() == null
                    || mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1) == null) {
                return false;
            }

            String d = mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1).getDisplayName();
            String c2 = "MYSTERY";
            String c1 = "MURDER";
            if (!d.contains(c1) && !d.contains(c2)) {
                return false;
            }

            Iterator var2 = Utils.Client.getPlayersFromScoreboard().iterator();

            while (var2.hasNext()) {
                String l = (String) var2.next();
                String s = Utils.Java.str(l);
                String c3 = "Role:";
                if (s.contains(c3)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void c() {
        mur.clear();
        det.clear();
    }
}
