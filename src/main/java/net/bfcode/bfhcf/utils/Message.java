package net.bfcode.bfhcf.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.chat.Text;
import net.bfcode.bfhcf.HCFaction;

import java.util.UUID;
import java.util.HashMap;

public class Message
{
    private HashMap<UUID, Long> messageDelay;
    HCFaction plugin;
    
    public Message(HCFaction plugin) {
        this.messageDelay = new HashMap<UUID, Long>();
        this.plugin = plugin;
    }
    
    public void sendMessage(Player player, String message) {
        if (this.messageDelay.containsKey(player.getUniqueId())) {
            if (this.messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0L) {
                return;
            }
            this.messageDelay.remove(player.getUniqueId());
        }
        this.messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000L);
        player.sendMessage(message);
    }
    
    public void sendMessage(Player player, Text text) {
        if (this.messageDelay.containsKey(player.getUniqueId())) {
            if (this.messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0L) {
                return;
            }
            this.messageDelay.remove(player.getUniqueId());
        }
        this.messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000L);
        text.send((CommandSender)player);
    }
    
    public static void sendMessage(String message, String permission) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission(permission)) {
                online.sendMessage(message);
            }
        }
    }
}
