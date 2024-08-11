package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow public GuiIngame ingameGUI;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;clearChatMessages()V"), method = "displayGuiScreen")
    public void onSetScreenChatClear(CallbackInfo ci) {
        ((GuiIngameMixed) this.ingameGUI).getReverseChat().clearChatMessages();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;clearChatMessages()V"), method = "runTick")
    public void onTickChatClear(CallbackInfo ci) {
        ((GuiIngameMixed) this.ingameGUI).getReverseChat().clearChatMessages();
    }

}
