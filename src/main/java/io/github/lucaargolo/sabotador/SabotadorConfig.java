package io.github.lucaargolo.sabotador;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lucaargolo.sabotador.utils.ScreenPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;

public class SabotadorConfig implements IModGuiFactory {

    private static final String CONFIG_PATH = Loader.instance().getConfigDir().toString() + "/sabotador.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static SabotadorConfig config = new SabotadorConfig();

    private boolean serverIndicatorEnabled = true;
    private String indicatorMessage = "Você está jogando no Sabotador.com";
    private EnumChatFormatting indicatorColor = EnumChatFormatting.GREEN;
    private boolean autoGGEnabled = true;
    private String autoGGMessage = "gg";
    private boolean autoTrustDetEnabled = true;
    private boolean secondChatEnabled = true;
    private boolean autoDropUntrust = false;
    private boolean autoDropTrust = false;
    private boolean autoDropNeutral = false;
    private boolean autoDropShop = false;
    private ScreenPosition chatPosition = ScreenPosition.TOP_LEFT;
    private String[] secondChatFilter = new String[]{"[▲]", "[?]", "[▼]"};
    private String[] bothChatsFilter = new String[]{"[✕]"};

    public static SabotadorConfig getConfig() {
        return config;
    }

    public boolean isServerIndicatorEnabled() {
        return serverIndicatorEnabled;
    }

    public String getIndicatorMessage() {
        return indicatorMessage;
    }

    public EnumChatFormatting getIndicatorColor() {
        return indicatorColor;
    }

    public boolean isAutoGGEnabled() {
        return autoGGEnabled;
    }

    public String getAutoGGMessage() {
        return autoGGMessage;
    }

    public boolean isAutoTrustDetEnabled() {
        return autoTrustDetEnabled;
    }

    public boolean isSecondChatEnabled() {
        return secondChatEnabled;
    }

    public boolean isAutoDropUntrust() {
        return autoDropUntrust;
    }

    public boolean isAutoDropTrust() {
        return autoDropTrust;
    }

    public boolean isAutoDropNeutral() {
        return autoDropNeutral;
    }

    public boolean isAutoDropShop() {
        return autoDropShop;
    }

    public ScreenPosition getChatPosition() {
        return chatPosition;
    }

    public String[] getSecondChatFilter() {
        return secondChatFilter;
    }

    public String[] getBothChatsFilter() {
        return bothChatsFilter;
    }

    public void save() {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(CONFIG_PATH)), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            SabotadorMod.LOGGER.error("[Sabotador] Failed to write config at: {}", CONFIG_PATH, e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void load() {
        SabotadorConfigGuiScreen.getConfigElements();
        try (Reader reader = new InputStreamReader(Files.newInputStream(Paths.get(CONFIG_PATH)), StandardCharsets.UTF_8)) {
            Type mapType = new TypeToken<SabotadorConfig>(){}.getType();
            config = GSON.fromJson(reader, mapType);
        } catch (Exception e) {
            SabotadorMod.LOGGER.error("[Sabotador] Failed to load config at: {}", CONFIG_PATH, e);
            if (tryCreateFile(CONFIG_PATH)) {
                config = new SabotadorConfig();
                config.save();
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "SameParameterValue"})
    private static boolean tryCreateFile(String p) {
        Path filePath = FileSystems.getDefault().getPath(p);
        String[] ps = p.split("/");
        StringBuilder fs = new StringBuilder();
        for(int i = 0; i < ps.length-1; i++) {
            fs.append(ps[i]).append("/");
        }

        Path folderPath = FileSystems.getDefault().getPath(fs.toString());
        try {
            folderPath.toFile().mkdirs();
            filePath.toFile().createNewFile();
        } catch (IOException ex) {
            SabotadorMod.LOGGER.error("[QEconomy] Failed to create client config {}", p, ex);
            return false;
        }
        return true;
    }

    public static class SabotadorConfigGuiScreen extends GuiConfig {

        private static final String[] VALID_COLORS = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        private static final String[] VALID_POSITIONS = new String[] {"TOP_LEFT", "TOP_RIGHT", "BOTTOM_RIGHT"};

        private static final Property serverIndicatorProperty = new Property("Server Indicator", Boolean.toString(config.serverIndicatorEnabled), Property.Type.BOOLEAN);
        private static final Property indicatorMessageProperty = new Property("Indicator Message", config.indicatorMessage, Property.Type.STRING);
        private static final Property indicatorColorProperty = new Property("Indicator Color", VALID_COLORS[config.indicatorColor.getColorIndex()], Property.Type.COLOR).setValidValues(VALID_COLORS);

        private static final Property autoGGProperty = new Property("Auto GG", Boolean.toString(config.autoGGEnabled), Property.Type.BOOLEAN);
        private static final Property autoGGMessageProperty = new Property("GG Message", config.autoGGMessage, Property.Type.STRING);

        private static final Property autoTrustDetProperty = new Property("Auto Trust Detective", Boolean.toString(config.autoTrustDetEnabled), Property.Type.BOOLEAN);

        private static final Property secondChatProperty = new Property("Second Chat", Boolean.toString(config.secondChatEnabled), Property.Type.BOOLEAN);
        private static final Property chatPositionProperty = new Property("Chat Position", config.chatPosition.name(), Property.Type.STRING).setValidValues(VALID_POSITIONS);
        private static final Property secondChatFilterProperty = new Property("Second Chat Filter", config.secondChatFilter, Property.Type.STRING);
        private static final Property bothChatsFilterProperty = new Property("Both Chats Filter", config.bothChatsFilter, Property.Type.STRING);

        private static final Property autoDropUntrustProperty = new Property("Auto Drop Untrust", Boolean.toString(config.autoDropUntrust), Property.Type.BOOLEAN);
        private static final Property autoDropTrustProperty = new Property("Auto Drop Trust", Boolean.toString(config.autoDropTrust), Property.Type.BOOLEAN);
        private static final Property autoDropNeutralProperty = new Property("Auto Drop Neutral", Boolean.toString(config.autoDropNeutral), Property.Type.BOOLEAN);
        private static final Property autoDropShopProperty = new Property("Auto Drop Shop", Boolean.toString(config.autoDropShop), Property.Type.BOOLEAN);


        public SabotadorConfigGuiScreen(GuiScreen parent) {
            super(parent, getConfigElements(), SabotadorMod.MOD_ID, false, false, "Sabotador Helper Config");
        }

        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> list = new ArrayList<IConfigElement>();

            serverIndicatorProperty.set(config.serverIndicatorEnabled);
            list.add(new ConfigElement(serverIndicatorProperty));
            indicatorMessageProperty.set(config.indicatorMessage);
            list.add(new ConfigElement(indicatorMessageProperty));
            indicatorColorProperty.set(VALID_COLORS[config.indicatorColor.getColorIndex()]);
            list.add(new ConfigElement(indicatorColorProperty));
            autoGGProperty.set(config.autoGGEnabled);
            list.add(new ConfigElement(autoGGProperty));
            autoGGMessageProperty.set(config.autoGGMessage);
            list.add(new ConfigElement(autoGGMessageProperty));
            autoTrustDetProperty.set(config.autoTrustDetEnabled);
            list.add(new ConfigElement(autoTrustDetProperty));
            secondChatProperty.set(config.secondChatEnabled);
            list.add(new ConfigElement(secondChatProperty));
            chatPositionProperty.set(config.chatPosition.name());
            list.add(new ConfigElement(chatPositionProperty));
            secondChatFilterProperty.set(config.secondChatFilter);
            list.add(new ConfigElement(secondChatFilterProperty));
            bothChatsFilterProperty.set(config.bothChatsFilter);
            list.add(new ConfigElement(bothChatsFilterProperty));
            autoDropUntrustProperty.set(config.autoDropUntrust);
            list.add(new ConfigElement(autoDropUntrustProperty));
            autoDropTrustProperty.set(config.autoDropTrust);
            list.add(new ConfigElement(autoDropTrustProperty));
            autoDropNeutralProperty.set(config.autoDropNeutral);
            list.add(new ConfigElement(autoDropNeutralProperty));;
            autoDropShopProperty.set(config.autoDropShop);
            list.add(new ConfigElement(autoDropShopProperty));
            return list;
        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
            config.serverIndicatorEnabled = serverIndicatorProperty.getBoolean();
            config.indicatorMessage = indicatorMessageProperty.getString();
            config.indicatorColor = EnumChatFormatting.values()[Arrays.asList(VALID_COLORS).indexOf(indicatorColorProperty.getString())];
            config.autoGGEnabled = autoGGProperty.getBoolean();
            config.autoGGMessage = autoGGMessageProperty.getString();
            config.autoTrustDetEnabled = autoTrustDetProperty.getBoolean();
            config.secondChatEnabled = secondChatProperty.getBoolean();
            config.chatPosition = ScreenPosition.valueOf(chatPositionProperty.getString());
            config.secondChatFilter = secondChatFilterProperty.getStringList();
            config.bothChatsFilter = bothChatsFilterProperty.getStringList();
            config.autoDropUntrust = autoDropUntrustProperty.getBoolean();
            config.autoDropTrust = autoDropTrustProperty.getBoolean();
            config.autoDropNeutral = autoDropNeutralProperty.getBoolean();
            config.autoDropShop = autoDropShopProperty.getBoolean();

            config.save();
        }
    }


    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return SabotadorConfigGuiScreen.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

}