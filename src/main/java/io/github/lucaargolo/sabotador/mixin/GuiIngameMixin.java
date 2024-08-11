package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.gui.ReverseGuiNewChat;
import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GuiIngame.class)
public abstract class GuiIngameMixin implements GuiIngameMixed {

    @Unique private ReverseGuiNewChat reverseGuiNewChat = new ReverseGuiNewChat();

    @Override
    public ReverseGuiNewChat getReverseChat() {
        return reverseGuiNewChat;
    }
}
