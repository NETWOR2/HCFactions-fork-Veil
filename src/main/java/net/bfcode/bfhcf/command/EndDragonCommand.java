package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;
import java.awt.image.BufferedImage;
import org.bukkit.Sound;

import javax.imageio.ImageIO;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.imagemessage.ImageChar;
import net.bfcode.bfbase.util.imagemessage.ImageMessage;
import net.bfcode.bfhcf.HCFaction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class EndDragonCommand implements CommandExecutor, TabCompleter
{
    private HCFaction plugin;
    
    public EndDragonCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (((Player)sender).getLocation().getWorld().getEnvironment() != World.Environment.THE_END) {
            sender.sendMessage(ChatColor.RED + "You must be in the end.");
            return true;
        }
        ((Player)sender).getWorld().spawnCreature(((Player)sender).getLocation(), EntityType.ENDER_DRAGON);
        for (Player on : Bukkit.getServer().getOnlinePlayers()) {
            for (int i = 0; i < 5; ++i) {
                on.sendMessage("");
            }
            try {
                BufferedImage imageToSend = ImageIO.read(this.plugin.getResource("enderdragon-art.png"));
                new ImageMessage(imageToSend, 15, ImageChar.BLOCK.getChar()).appendText(new String[] { "", "", "", "", "", "", ChatColor.RED + "[EnderDragon]", ChatColor.YELLOW + "Spawned" }).sendToPlayer(on);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            on.playSound(on.getLocation(), Sound.ENDERDRAGON_GROWL, 3.0f, 5.0f);
        }
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
