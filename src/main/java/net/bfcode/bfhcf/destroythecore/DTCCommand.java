package net.bfcode.bfhcf.destroythecore;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfbase.util.Ints;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class DTCCommand implements CommandExecutor {
	
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.no_console);
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("hcf.command." + label + "")) {
            player.sendMessage(Utils.no_permission);
            return true;
        }
        if (args.length < 1) {
            this.getUsage(player, label);
        }
        else if (args[0].equalsIgnoreCase("wand")) {
            player.getInventory().addItem(new ItemStack[] { DTCHandler.getDTCWand() });
            player.sendMessage(CC.translate("&eYou have received the &aDTC Wand&e."));
        }
        else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 3) {
                this.getUsage(player, label);
                return true;
            }
            String DTC = args[1];
            if (DTC.length() > 10) {
                player.sendMessage(CC.translate("&cMax name is 10 length."));
                return true;
            }
            if (!DTCHandler.isSetSelection()) {
                player.sendMessage(CC.translate("&cFirst, you must select the block."));
                return true;
            }
            if (DTCHandler.isAlreadyCreated(DTC)) {
                player.sendMessage(CC.translate("&cDTC '" + DTC + " is already created."));
                return true;
            }
            Integer point = Ints.tryParse(args[2]);
            if (point == null) {
                player.sendMessage(CC.translate("&c'" + args[2] + "' is not a valid number."));
                return true;
            }
            if (point <= 0) {
                player.sendMessage(CC.translate("&cThe Point must be positive."));
                return true;
            }
            DTCHandler.createDTC(DTC, point);
            player.sendMessage(CC.translate("&eYou have created &a" + DTC + " DTC Event &esuccessfully."));
        }
        else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                this.getUsage(player, label);
                return true;
            }
            String DTC = args[1];
            if (!DTCHandler.isAlreadyCreated(DTC)) {
                player.sendMessage(CC.translate("&cDTC '" + DTC + "' not created."));
                return true;
            }
            DTCHandler.deleteDTC(DTC);
            player.sendMessage(CC.translate("&eYou have deleted &a" + DTC + " DTC Event &esucessfully."));
        }
        else if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(CC.translate("&eDTC Event List: &f" + DTCHandler.getDTCList().toString().replace("[", "").replace("]", "")));
        }
        else if (args[0].equalsIgnoreCase("start")) {
            if (args.length < 2) {
            	this.getUsage(player, label);
                return true;
            }
            String DTC = args[1];
            if (!DTCHandler.isAlreadyCreated(DTC)) {
                player.sendMessage(CC.translate("&cDTC '" + DTC + "' not created."));
                return true;
            }
            if (DTCHandler.isStarted(DTC)) {
                player.sendMessage(CC.translate("&cDTC Event '" + DTC + "' is already started."));
                return true;
            }
            DTCHandler.setDTCEvent(DTC, true);
            Bukkit.broadcastMessage(CC.translate(HCFaction.getPlugin().getConfig().getString("DestroyTheCore.Started")
            		.replace("%playername%", player.getName())
            		.replace("%destroythecore%", DTC)));
        }
        else if (args[0].equalsIgnoreCase("stop")) {
            if (args.length < 2) {
                this.getUsage(player, label);
                return true;
            }
            String DTC = args[1];
            if (!DTCHandler.isAlreadyCreated(DTC)) {
                player.sendMessage(CC.translate("&cDTC '" + DTC + "' not created."));
                return true;
            }
            if (!DTCHandler.isStarted(DTC)) {
                player.sendMessage(CC.translate("&cDTC Event '" + DTC + "' is already stopped."));
                return true;
            }
            DTCHandler.setDTCEvent(DTC, false);
            Bukkit.broadcastMessage(CC.translate(HCFaction.getPlugin().getConfig().getString("DestroyTheCore.Stopped")
            		.replace("%playername%", player.getName())
            		.replace("%destroythecore%", DTC)));
        }
        else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
            if (args.length < 2) {
                this.getUsage(player, label);
                return true;
            }
            String DTC = args[1];
            if (!DTCHandler.isAlreadyCreated(DTC)) {
                player.sendMessage(CC.translate("&cDTC '" + DTC + "' not created."));
                return true;
            }
            int x = DTCHandler.getDTCLocX(DTC);
            int y = DTCHandler.getDTCLocY(DTC);
            int z = DTCHandler.getDTCLocZ(DTC);
            String world = player.getWorld().getName();
            Location location = new Location(Bukkit.getWorld(world), (double)x, (double)y, (double)z);
            player.teleport(location);
            player.sendMessage(CC.translate("&eYou has been teleported to DTC: &f" + DTC));
        }
        else {
            player.sendMessage(CC.translate("&cDTC sub-command '" + args[0] + "' not found."));
        }
        return true;
    }
    
    private void getUsage(CommandSender sender, String label) {
        sender.sendMessage(CC.translate("&7&m------------------------------"));
        sender.sendMessage(CC.translate("&6&lDTC Commands &7(Page 1 of 1)"));
        sender.sendMessage("");
        sender.sendMessage(CC.translate(" &e/" + label + " list &7- &fList of all DTC Event."));
        sender.sendMessage(CC.translate(" &e/" + label + " wand &7- &fReceive a wand to create a DTC Event."));
        sender.sendMessage(CC.translate(" &e/" + label + " create <name> <amount> &7- &fCreate a DTC Event."));
        sender.sendMessage(CC.translate(" &e/" + label + " delete <name> &7- &fDelete a DTC Event."));
        sender.sendMessage(CC.translate(" &e/" + label + " start <name> &7- &fStart a DTC Event."));
        sender.sendMessage(CC.translate(" &e/" + label + " stop <name> &7- &fStop a DTC Event."));
        sender.sendMessage(CC.translate(" &e/" + label + " teleport <name> &7- &fTeleport to a DTC Event."));
        sender.sendMessage(CC.translate("&7&m------------------------------"));
    }
}
