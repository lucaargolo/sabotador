package io.github.lucaargolo.sabotador.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerControllerMP.class)
public interface PlayerControllerMPAccessor {

    @Invoker
    public void invokeSyncCurrentPlayItem();

}
