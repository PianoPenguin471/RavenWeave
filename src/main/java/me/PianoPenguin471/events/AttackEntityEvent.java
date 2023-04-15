package me.PianoPenguin471.events;

import club.maxstats.weave.loader.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AttackEntityEvent extends Event {
    public Entity target;
    public EntityPlayer player;
    public Minecraft mc;

    public AttackEntityEvent(Minecraft mc, Entity targetEntity, EntityPlayer playerIn) {
        this.mc = mc;
        this.target = targetEntity;
        this.player = playerIn;

    }
}
