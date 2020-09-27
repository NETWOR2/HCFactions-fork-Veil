package net.bfcode.bfhcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.Inventory;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.ItemBuilder;
import net.bfcode.bfhcf.HCFaction;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

@SuppressWarnings("unused")
public class SpawnerCommand implements CommandExecutor, TabCompleter {
	
	private HCFaction plugin;
    public SpawnerCommand(HCFaction plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/spawner <entity>");
            return false;
        }
        String spawner = args[0];
        Player p = (Player)sender;
        Inventory inv = (Inventory)p.getInventory();
        inv.addItem(new ItemStack[] { new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + spawner + " Spawner").loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(spawner)).build() });
        p.sendMessage(CC.translate("&eYou just got a &a" + spawner + " Spawner&e."));
        return false;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
