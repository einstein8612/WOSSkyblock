package net.worldofsurvival.wosskyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public final class Common {
    private static Class<?> CRAFTPLAYERCLASS, PACKET_PLAYER_CHAT_CLASS, ICHATCOMP, CHATMESSAGE, PACKET_CLASS, CHAT_MESSAGE_TYPE_CLASS;
    private static Field PLAYERCONNECTION;
    private static Method GETHANDLE,SENDPACKET;
    private static Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR, CHATMESSAGE_CONSTRUCTOR;
    private static Object CHAT_MESSAGE_TYPE_ENUM_OBJECT;
    private static final String SERVER_VERSION;
    static {
        String name = Bukkit.getServer().getClass().getName();
        name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
        name = name.substring(0, name.indexOf("."));
        SERVER_VERSION = name;
 
        try {
            CRAFTPLAYERCLASS = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
            PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
            PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
            ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
            GETHANDLE = CRAFTPLAYERCLASS.getMethod("getHandle");
            PLAYERCONNECTION = GETHANDLE.getReturnType().getField("playerConnection");
            SENDPACKET = PLAYERCONNECTION.getType().getMethod("sendPacket", PACKET_CLASS);
            try {
                PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
            } catch (NoSuchMethodException exception) {
                CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessageType");
                CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];
                PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, CHAT_MESSAGE_TYPE_CLASS);
            }
            CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");
            CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
        } catch (Exception exception) { exception.printStackTrace(); }
    }
    public static void sendHotBarMessage(Player player, String message) {
        try {
            Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message, new Object[0]);
            Object packet;
            try {
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
            } catch (Exception exception) {
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
            }
            Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
            Object methodhHandle = GETHANDLE.invoke(craftplayerInst);
            Object playerConnection = PLAYERCONNECTION.get(methodhHandle);
            SENDPACKET.invoke(playerConnection, packet);
        } catch (Exception exception) { exception.printStackTrace(); }
    }
}
