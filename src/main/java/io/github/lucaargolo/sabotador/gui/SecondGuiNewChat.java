package io.github.lucaargolo.sabotador.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

import io.github.lucaargolo.sabotador.SabotadorConfig;
import io.github.lucaargolo.sabotador.utils.ScreenPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecondGuiNewChat extends Gui {

    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;

    public void drawChat(int updateCounter) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.getLineCount();
            boolean flag = false;
            int j = 0;
            int k = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (k > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }

                float f1 = this.getChatScale();
                int l = MathHelper.ceiling_float_int((float)this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);

                    if (chatline != null) {
                        int j1 = updateCounter - chatline.getUpdatedCounter();

                        if (j1 < 200 || flag) {
                            double d0 = (double)j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int)(255.0D * d0);

                            if (flag) {
                                l1 = 255;
                            }

                            l1 = (int)((float)l1 * f);
                            ++j;

                            if (l1 > 3) {
                                int i2 = 0;
                                int j2 = -i1 * 9;
                                drawRect(i2, j2 - 9, i2 + l + 4, j2, l1 / 2 << 24);
                                String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(s, (float)i2, (float)(j2 - 8), 16777215 + (l1 << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (flag) {
                    int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = k * k2 + k;
                    int i3 = j * k2 + j;
                    int j3 = this.scrollPos * i3 / k;
                    int k1 = i3 * i3 / l2;

                    if (l2 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        if(SabotadorConfig.getConfig().getChatPosition() == ScreenPosition.BOTTOM_RIGHT) {
                            int w = getChatWidth();
                            drawRect(w-2, -j3, w, -j3 - k1, l3 + (k3 << 24));
                            drawRect(w, -j3, w-1, -j3 - k1, 13421772 + (k3 << 24));
                        }else {
                            drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                            drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                        }
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    public void drawChatReversed(int ticks) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.getLineCount();
            boolean bl = false;
            int j = 0;
            int k = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (k > 0) {
                if (this.getChatOpen()) {
                    bl = true;
                }

                float g = this.getChatScale();
                int l = MathHelper.ceiling_float_int((float)this.getChatWidth() / g);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(g, g, 1.0F);

                // Start the rendering from the top
                for (int m = 0; m + this.scrollPos < this.drawnChatLines.size() && m < i; m++) {
                    ChatLine chatHudLine = this.drawnChatLines.get(m + this.scrollPos);
                    if (chatHudLine != null) {
                        int n = ticks - chatHudLine.getUpdatedCounter();
                        if (n < 200 || bl) {
                            double d = (double)n / 200.0;
                            d = 1.0 - d;
                            d *= 10.0;
                            d = MathHelper.clamp_double(d, 0.0, 1.0);
                            d *= d;
                            int o = (int)(255.0 * d);
                            if (bl) {
                                o = 255;
                            }

                            o = (int)((float)o * f);
                            j++;
                            if (o > 3) {
                                int p = 0;
                                // Calculate q based on the message index to render from top to bottom
                                int q = m * 9;
                                drawRect(p, q, p + l + 4, q + 9, o / 2 << 24);
                                String string = chatHudLine.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(string, (float)p, (float)(q + 1), 16777215 + (o << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (bl) {
                    int mx = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int r = k * mx + k;
                    int n = j * mx + j;
                    int s = this.scrollPos * n / k;
                    int t = n * n / r;
                    if (r != n) {
                        int ox = s > 0 ? 170 : 96;
                        int p = this.isScrolled ? 13382451 : 3355562;
                        if(SabotadorConfig.getConfig().getChatPosition() == ScreenPosition.TOP_RIGHT) {
                            int w = getChatWidth();
                            drawRect(w-2, s, w, s + t, p + (ox << 24));
                            drawRect(w, s, w-1, s + t, 13421772 + (ox << 24));
                        }else{
                            drawRect(0, s, 2, s + t, p + (ox << 24));
                            drawRect(2, s, 1, s + t, 13421772 + (ox << 24));
                        }

                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Clears the chat.
     */
    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
    }

    public void printChatMessage(IChatComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[SAB] " + chatComponent.getUnformattedText());
    }

    private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }

        int i = MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale());
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRendererObj, false, false);
        boolean flag = this.getChatOpen();

        for (IChatComponent ichatcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, chatLineId));
        }

        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; --i)
        {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     *
     * @param amount The amount to scroll
     */
    public void scroll(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();

        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    public IChatComponent getChatComponent(int mouseX, int mouseY) {
        return null;
        //TODO: This
//        if (!this.getChatOpen()) {
//            return null;
//        } else {
//            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
//            int i = scaledresolution.getScaleFactor();
//            float f = this.getChatScale();
//            int j = mouseX / i - 3;
//            int k = mouseY / i - 27;
//            j = MathHelper.floor_float((float)j / f);
//            k = MathHelper.floor_float((float)k / f);
//
//            if (j >= 0 && k >= 0) {
//                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
//
//                if (j <= MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
//                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
//
//                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
//                        ChatLine chatline = this.drawnChatLines.get(i1);
//                        int j1 = 0;
//
//                        for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
//                            if (ichatcomponent instanceof ChatComponentText) {
//                                j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false));
//
//                                if (j1 > j) {
//                                    return ichatcomponent;
//                                }
//                            }
//                        }
//                    }
//
//                    return null;
//                } else {
//                    return null;
//                }
//            } else {
//                return null;
//            }
//        }
    }

    /**
     * Returns true if the chat GUI is open
     */
    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    /**
     * finds and deletes a Chat line by ID
     *
     * @param id The ChatLine's id to delete
     */
    public void deleteChatLine(int id)
    {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();

        while (iterator.hasNext())
        {
            ChatLine chatline = iterator.next();

            if (chatline.getChatLineID() == id)
            {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext())
        {
            ChatLine chatline1 = iterator.next();

            if (chatline1.getChatLineID() == id)
            {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        int i = 320;
        int j = 40;
        return MathHelper.floor_float(scale * (float)(i - j) + (float)j);
    }

    public static int calculateChatboxHeight(float scale) {
        int i = 180;
        int j = 20;
        return MathHelper.floor_float(scale * (float)(i - j) + (float)j);
    }

    public int getLineCount()
    {
        return this.getChatHeight() / 9;
    }
}