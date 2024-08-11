package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.SabotadorConfig;
import io.github.lucaargolo.sabotador.SabotadorMod;
import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {

    @Shadow private Minecraft gameController;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/S02PacketChat;getType()B", ordinal = 1), method = "handleChat", cancellable = true)
    public void captureSabMessages(S02PacketChat packetIn, CallbackInfo ci) {
        if (SabotadorMod.isOnSabotador() && SabotadorConfig.getConfig().isSecondChatEnabled() && packetIn.getType() != 2) {
            IChatComponent message = packetIn.getChatComponent();
            String u = message.getUnformattedText();
            for (String s : SabotadorConfig.getConfig().getSecondChatFilter()) {
                if(u.startsWith(s)) {
                    ((GuiIngameMixed) this.gameController.ingameGUI).getSecondChat().printChatMessage(message);
                    ci.cancel();
                    break;
                }
            }
            for (String s : SabotadorConfig.getConfig().getBothChatsFilter()) {
                if(u.startsWith(s)) {
                    ((GuiIngameMixed) this.gameController.ingameGUI).getSecondChat().printChatMessage(message);
                    break;
                }
            }
        }
    }

}
