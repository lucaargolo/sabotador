package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.SabotadorConfig;
import io.github.lucaargolo.sabotador.SabotadorMod;
import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import io.github.lucaargolo.sabotador.utils.ScreenPosition;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen {

    @Inject(at = @At("HEAD"), method = "onGuiClosed")
    public void handleSabChatRemoved(CallbackInfo ci) {
        ((GuiIngameMixed) this.mc.ingameGUI).getSecondChat().resetScroll();
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    public void handleSabChatClick(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button == 0) {
            IChatComponent ichatcomponent = ((GuiIngameMixed) this.mc.ingameGUI).getSecondChat().getChatComponent(Mouse.getX(), Mouse.getY());
            if (this.handleComponentClick(ichatcomponent)) {
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "handleMouseInput", cancellable = true)
    public void handleSabChatScroll(CallbackInfo ci) {
        int i = Mouse.getEventDWheel();
        if (SabotadorMod.isOnSabotadorFast() && SabotadorConfig.getConfig().isSecondChatEnabled() && i != 0) {
            ScreenPosition position = SabotadorConfig.getConfig().getChatPosition();
            if(position == ScreenPosition.TOP_LEFT || position == ScreenPosition.TOP_RIGHT) {
                if (i > 1) {
                    i = -1;
                }

                if (i < -1) {
                    i = 1;
                }
            }else{
                if (i > 1) {
                    i = 1;
                }

                if (i < -1) {
                    i = -1;
                }
            }


            if (!isShiftKeyDown()) {
                i *= 7;
            }
            if(((position == ScreenPosition.TOP_LEFT || position == ScreenPosition.TOP_RIGHT ) && Mouse.getY() > this.mc.displayHeight/2) || (position == ScreenPosition.BOTTOM_RIGHT && Mouse.getX() > this.mc.displayWidth/2)) {
                ((GuiIngameMixed) this.mc.ingameGUI).getSecondChat().scroll(i);
                ci.cancel();
            }
        }
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;drawTextBox()V", shift = At.Shift.AFTER, by = 1), method = "drawScreen")
    public void handleSabChatHoverText(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        IChatComponent ichatcomponent = ((GuiIngameMixed) this.mc.ingameGUI).getSecondChat().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }
    }


}
