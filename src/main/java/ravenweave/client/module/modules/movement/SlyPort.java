package ravenweave.client.module.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.world.AntiBot;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.SliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.util.Iterator;

public class SlyPort extends Module {
    public static DescriptionSetting f;
    public static SliderSetting range;
    public static TickSetting sound;
    public static TickSetting players;
    public static TickSetting aim;

    public SlyPort() {
        super("SlyPort", ModuleCategory.movement);
        this.registerSetting(new DescriptionSetting("Teleport behind enemies."));
        this.registerSetting(range = new SliderSetting("Range", 6.0D, 2.0D, 15.0D, 1.0D));
        this.registerSetting(aim = new TickSetting("Aim", true));
        this.registerSetting(sound = new TickSetting("Play sound", true));
        this.registerSetting(players = new TickSetting("Players only", true));
    }

    @Override
    public void onEnable() {
        Entity en = this.ge();
        if (en != null)
            this.tp(en);

        this.disable();
    }

    private void tp(Entity en) {
        if (sound.isToggled())
            mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 1.0F);

        Vec3 vec = en.getLookVec();
        double x = en.posX - (vec.xCoord * 2.5D);
        double z = en.posZ - (vec.zCoord * 2.5D);
        mc.thePlayer.setPosition(x, mc.thePlayer.posY, z);
        if (aim.isToggled())
            Utils.Player.aim(en, 0.0F);

    }

    private Entity ge() {
        Entity en = null;
        double r = Math.pow(SlyPort.range.getInput(), 2.0D);
        double dist = r + 1.0D;
        Iterator<Entity> var6 = mc.theWorld.loadedEntityList.iterator();

        while (true) {
            Entity ent;
            do
                do
                    do
                        do {
                            if (!var6.hasNext())
                                return en;

                            ent = var6.next();
                        } while (ent == mc.thePlayer);
                    while (!(ent instanceof EntityLivingBase));
                while (((EntityLivingBase) ent).deathTime != 0);
            while (players.isToggled() && !(ent instanceof EntityPlayer));

            if (!AntiBot.bot(ent)) {
                double d = mc.thePlayer.getDistanceSqToEntity(ent);
                if (!(d > r) && !(dist < d)) {
                    dist = d;
                    en = ent;
                }
            }
        }
    }
}
