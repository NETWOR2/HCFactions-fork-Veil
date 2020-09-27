package net.bfcode.bfhcf.faction.argument.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.Message;
import net.bfcode.bfhcf.utils.Utils;

public class FactionRemovePointsArgument extends CommandArgument {
	
    public FactionRemovePointsArgument() {
        super("removepoints", "remove points to a faction");
        this.permission = "hcf.command.faction.argument." + this.getName();
    }

	@Override
	public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <factionName> <points>";
	}
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(CC.translate("&cCorrect Usage: /f removepoints <factionName> <points>."));
            return true;
        }
        int newPoints = Integer.valueOf(args[2]);
        if (newPoints < 0) {
            sender.sendMessage(CC.translate("&cInvalid number"));
            return true;
        }
        Faction faction = HCFaction.getPlugin().getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(Utils.faction_not_found);
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(CC.translate("&cThis type of faction does not use points."));
            return true;
        }
        PlayerFaction playerFaction = (PlayerFaction)faction;
        int previousPoints = playerFaction.getPoints();
        playerFaction.setPoints(previousPoints - newPoints);
        Message.sendMessage(CC.translate("&a&l" + sender.getName() + "&asuccsessfully removed &l" + newPoints + " &eto the faction &f" + faction.getName() + "&e."), "hcf.admin");
        return true;
    }
}
