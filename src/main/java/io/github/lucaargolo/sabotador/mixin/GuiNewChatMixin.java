package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.SabotadorConfig;
import io.github.lucaargolo.sabotador.utils.ScreenPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin {


    @Shadow @Final private Minecraft mc;

    @Inject(at = @At("RETURN"), method = "getChatWidth", cancellable = true)
    public void getChatWidth(CallbackInfoReturnable<Integer> cir) {
        int chatWidth = cir.getReturnValue();
        if(SabotadorConfig.getConfig().getChatPosition() == ScreenPosition.BOTTOM_RIGHT) {
            int scaledWidth = ((GuiIngameForge) this.mc.ingameGUI).getResolution().getScaledWidth();
            cir.setReturnValue(Math.min(chatWidth, scaledWidth/2 - 20));
        }

    }


    @Inject(at = @At("RETURN"), method = "getChatHeight", cancellable = true)
    public void getChatHeight(CallbackInfoReturnable<Integer> cir) {
        int chatHeight =  cir.getReturnValue();
        if(SabotadorConfig.getConfig().getChatPosition() == ScreenPosition.TOP_RIGHT || SabotadorConfig.getConfig().getChatPosition() == ScreenPosition.TOP_LEFT) {
            int scaledHeight = ((GuiIngameForge) this.mc.ingameGUI).getResolution().getScaledHeight();
            cir.setReturnValue(Math.min(chatHeight, scaledHeight/2 - 20));
        }
    }


}
