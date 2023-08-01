package me.pianopenguin471.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface TimerAccessor {

    // i love accessors how tf do i use them

    @Accessor
    Timer getTimer();

}
