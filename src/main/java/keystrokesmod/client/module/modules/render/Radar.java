package keystrokesmod.client.module.modules.render;

import net.weavemc.loader.api.event.RenderGameOverlayEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.world.AntiBot;
import keystrokesmod.client.module.setting.impl.RGBSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;

public class Radar extends Module {

    private final SliderSetting distance;
    private final RGBSetting boxColor;
    private final RGBSetting boarderColor;
    private final RGBSetting playerColor;
    private final RGBSetting selfColor;

    public Radar() {
        super("Radar", /*ModuleCategory.render*/ModuleCategory.beta);
        this.registerSetting(distance = new SliderSetting("distance", 25, 5, 100, 1));
        this.registerSetting(boxColor = new RGBSetting("box color", 0, 200, 0));
        this.registerSetting(boarderColor = new RGBSetting("boarder color", 255, 200, 255));
        this.registerSetting(playerColor = new RGBSetting("player color", 0, 0, 255));
        this.registerSetting(selfColor = new RGBSetting("self color", 255, 0, 0));

    }

    @SubscribeEvent
    public void render2D(RenderGameOverlayEvent e) {
        if (!this.enabled) return;
        if(!Utils.Player.isPlayerInGame() || (mc.currentScreen != null))
            return;
        int height = 50;
        int width = 50;
        int y = 0;
        int x = 200;
        int centreX = x + (width /2), centreY = y + (height /2);
        RenderUtils.drawBorderedRoundedRect(x, y, x + width, y + height, 5, 5, boarderColor.getRGB(), boxColor.getRGB());
        for(Entity en : mc.theWorld.playerEntities) {
        	if((en == mc.thePlayer) || AntiBot.bot(en)) continue;
            int radius = (int) mc.thePlayer.getDistanceToEntity(en);
            if(radius > distance.getInput()) continue;
            int theta = (int) Utils.Player.fovFromEntity(en) -180; //why do i need to put the 180 here huh
            int 	enX = (int) ((radius * Math.sin(Math.toRadians(theta)))*((width /2)/distance.getInput())),
            		enY = (int) ((radius * Math.cos(Math.toRadians(theta)))*((height /2)/distance.getInput()));
            Gui.drawRect((centreX + enX) -1 , (centreY + enY) - 1 , centreX + enX + 1, centreY + enY + 1, playerColor.getRGB());
        }
        Gui.drawRect(centreX - 1, centreY - 1, centreX + 1, centreY + 1, selfColor.getRGB());
    }
}
