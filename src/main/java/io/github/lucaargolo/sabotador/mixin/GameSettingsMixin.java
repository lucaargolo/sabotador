package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class GameSettingsMixin {

    @Shadow protected Minecraft mc;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;refreshChat()V"), method = "setOptionFloatValue")
    public void onChatReset(CallbackInfo ci) {
        ((GuiIngameMixed) this.mc.ingameGUI).getSecondChat().refreshChat();
    }

}
