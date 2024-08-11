package io.github.lucaargolo.sabotador;

import io.github.lucaargolo.sabotador.mixed.GuiIngameMixed;
import io.github.lucaargolo.sabotador.mixin.GuiPlayerTabOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

@Mod(modid = SabotadorMod.MODID, useMetadata = true)
public class SabotadorMod {

    public static final String MODID = "sabotador";
    private static Tuple<String, Integer> currentMinigame = null;
    private static boolean isOnSabotador = false;
    private static GameState gameState = GameState.NONE;

    public enum GameState {
        NONE,
        LOBBY,
        STARTING,
        STARTED,
        FINISHED
    }

    public enum GameRole {
        NONE,
        MORTO,
        SUSPEITO,
        DETETIVE,
        INOCENTE,
        SABOTADOR
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            isOnSabotador = SabotadorMod.isOnSabotador();
            if(isOnSabotador) {
                currentMinigame = getCurrentMinigame();
                if(currentMinigame != null && currentMinigame.getFirst().contains("sab")) {
                    GameState current = getCurrentGameState();
                    GameState previous = gameState;
                    if(current != previous) {
                        onGameStateChange(current);
                    }
                    gameState = current;
                }else{
                    gameState = GameState.NONE;
                }
            }else{
                currentMinigame = null;
                gameState = GameState.NONE;
            }
        }
    }

    @SubscribeEvent
    public void onChat(RenderGameOverlayEvent.Chat event) {
        Minecraft client = Minecraft.getMinecraft();
        if(SabotadorMod.isOnSabotadorFast()) {
            GlStateManager.pushMatrix();

            GlStateManager.translate(0.0F, 8.0, 0.0F);
            ((GuiIngameMixed) client.ingameGUI).getReverseChat().drawChat(client.ingameGUI.getUpdateCounter());
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onHud(RenderGameOverlayEvent.Post event) {
        if(event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            Minecraft client = Minecraft.getMinecraft();
            FontRenderer textRenderer = client.fontRendererObj;
            Tuple<String, Integer> currentMinigame = SabotadorMod.getCurrentMinigameFast();
            if (currentMinigame != null) {
                textRenderer.drawStringWithShadow("Você está jogando no " + currentMinigame.getFirst() + "-" + currentMinigame.getSecond(), 7, 7, 0x00FF00);
            }
        }
    }

    private static void onGameStateChange(GameState gameState) {
        Minecraft client = Minecraft.getMinecraft();
        EntityPlayerSP player = client.thePlayer;
        if(player != null) {
            if (gameState == GameState.FINISHED) {
                player.sendChatMessage("gg");
                ChatComponentText separator = new ChatComponentText("══════════════════════════════");
                separator.setChatStyle(separator.getChatStyle().setBold(true).setStrikethrough(true).setColor(EnumChatFormatting.DARK_GREEN));
                ((GuiIngameMixed) client.ingameGUI).getReverseChat().printChatMessage(separator);
            }else if(gameState == GameState.STARTED) {
                Map<NetworkPlayerInfo, GameRole> gamePlayers = getGamePlayers();
                NetworkPlayerInfo selfEntry = player.sendQueue.getPlayerInfo(player.getUniqueID());
                GameRole currentRole = gamePlayers.getOrDefault(selfEntry, GameRole.NONE);
                if(currentRole != GameRole.NONE && currentRole != GameRole.MORTO) {
                    gamePlayers.forEach((entry, role) -> {
                        if (role == GameRole.DETETIVE) {
                            player.sendChatMessage("/c " + entry.getGameProfile().getName());
                        }
                    });
                }
            }
        }
    }

    public static Map<NetworkPlayerInfo, GameRole> getGamePlayers() {
        Minecraft client = Minecraft.getMinecraft();
        EntityPlayerSP player = client.thePlayer;
        if(player != null) {
            NetHandlerPlayClient connection = player.sendQueue;
            if(connection != null) {
                Map<NetworkPlayerInfo, GameRole> map = new HashMap<>();
                Collection<NetworkPlayerInfo> collection = connection.getPlayerInfoMap();
                for(NetworkPlayerInfo entry : collection) {
                    GameRole role = getGameRole(entry);
                    map.put(entry, role);
                }
                return map;
            }
        }
        return Collections.emptyMap();
    }

    public static GameRole getGameRole(NetworkPlayerInfo entry) {
        ScorePlayerTeam team = entry.getPlayerTeam();
        GameRole role;
        if(team != null) {
            String prefix = team.getColorPrefix().replaceAll("§.", "");
            if(prefix.startsWith("Morto")) {
                role = GameRole.MORTO;
            }else if(prefix.startsWith("Suspeito")) {
                role = GameRole.SUSPEITO;
            }else if(prefix.startsWith("Detetive")) {
                role = GameRole.DETETIVE;
            }else if(prefix.startsWith("Inocente")) {
                role = GameRole.INOCENTE;
            }else if(prefix.startsWith("Sabotador")) {
                role = GameRole.SABOTADOR;
            }else{
                role = GameRole.NONE;
            }
        }else{
            role = GameRole.NONE;
        }
        return role;
    }

    public static GameState getCurrentGameState() {
        Minecraft client = Minecraft.getMinecraft();
        WorldClient world = client.theWorld;
        if(world != null) {
            Scoreboard scoreboard = world.getScoreboard();
            if(scoreboard != null) {
                Optional<ScoreObjective> optional = scoreboard.getScoreObjectives().stream().filter(o -> o.getName().equals("side_display")).findFirst();
                if(optional.isPresent()) {
                    ScoreObjective objective = optional.get();
                    Collection<Score> collection = scoreboard.getSortedScores(objective);
                    StringBuilder builder = new StringBuilder();
                    for(Score score : collection) {
                        builder.append(score.getPlayerName()).append("\n");
                    }
                    String title = objective.getDisplayName().replaceAll("§.", "");
                    String body;
                    if(builder.length() > 0) {
                        body = builder.substring(0, builder.length() - 1).replaceAll("§.", "");
                    }else{
                        body = "";
                    }
                    if(title.contains("Começando") || title.contains("Aguardando")) {
                        return GameState.LOBBY;
                    }else if(body.contains("Pegue todos")) {
                        return GameState.STARTING;
                    }else if(title.contains("Confiança")) {
                        return GameState.STARTED;
                    }else if(title.contains("Melhores Jogadores")) {
                        return GameState.FINISHED;
                    }else {
                        return GameState.NONE;
                    }
                }
            }
        }
        return gameState;
    }

    public static GameState getCurrentGameStateFast() {
        return gameState;
    }

    public static Tuple<String, Integer> getCurrentMinigame() {
        Minecraft client = Minecraft.getMinecraft();
        GuiPlayerTabOverlayAccessor playerList = (GuiPlayerTabOverlayAccessor) client.ingameGUI.getTabList();
        IChatComponent footer = playerList.getFooter();
        if(footer != null) {
            String f = footer.getFormattedText().replaceAll("§.", "");
            String[] s1 = f.split("\n");
            if(s1.length > 2) {
                String[] s2 = s1[2].split("┃");
                if(s2.length > 1) {
                    String[] s3 = s2[0].split("-");
                    if(s3.length > 1) {
                        String minigame = s3[0];
                        String sanitizedRoom = s3[1].replace(" ", "");
                        try {
                            int room = Integer.parseInt(sanitizedRoom);
                            return new Tuple<>(minigame, room);
                        }catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Tuple<String, Integer> getCurrentMinigameFast() {
        return currentMinigame;
    }

    public static boolean isOnSabotador() {
        Minecraft client = Minecraft.getMinecraft();
        ServerData info = client.getCurrentServerData();
        if(info != null) {
            return info.serverIP.toLowerCase().contains("sabotador.com");
        }else{
            return false;
        }
    }

    public static boolean isOnSabotadorFast() {
        return isOnSabotador;
    }

}
