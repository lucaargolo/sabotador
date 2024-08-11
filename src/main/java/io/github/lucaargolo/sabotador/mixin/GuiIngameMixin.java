package io.github.lucaargolo.sabotador.mixin;

import io.github.lucaargolo.sabotador.gui.SecondGuiNewChat;
import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GuiIngame.class)
public abstract class GuiIngameMixin implements GuiIngameMixed {

    @Unique private SecondGuiNewChat secondGuiNewChat = new SecondGuiNewChat();

    @Override
    public SecondGuiNewChat getSecondChat() {
        return secondGuiNewChat;
    }
}
