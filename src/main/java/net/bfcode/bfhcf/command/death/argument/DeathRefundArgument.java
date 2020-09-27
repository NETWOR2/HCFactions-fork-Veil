package net.bfcode.bfhcf.command.death.argument;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.listener.DeathListener;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class DeathRefundArgument extends CommandArgument
{
    public DeathRefundArgument(HCFaction plugin) {
        super("refund", "Rollback an inventory");
        this.permission = "hcf.command.death.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <playerName> <reason>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
            return true;
        }
        if (Bukkit.getPlayer(args[1]) == null) {
            p.sendMessage(ChatColor.RED + "Player isn't online.");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())) {
            target.getInventory().setContents((ItemStack[])DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
            target.getInventory().setArmorContents((ItemStack[])DeathListener.PlayerArmorContents.get(target.getUniqueId()));
            String reason = StringUtils.join(args, ' ', 2, args.length);
            Command.broadcastCommandMessage((CommandSender)p, ChatColor.YELLOW + "Returned " + target.getName() + "'s items for: " + reason);
            DeathListener.PlayerArmorContents.remove(target.getUniqueId());
            DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
            return true;
        }
        p.sendMessage(ChatColor.RED + "Inventory not found. (Already rolled back?)");
        return false;
    }
}
