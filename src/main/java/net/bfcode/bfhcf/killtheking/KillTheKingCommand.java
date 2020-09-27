package net.bfcode.bfhcf.killtheking;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;

public class KillTheKingCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("hcf.command.killtheking")) {
			return true;
		}
		if (!(sender instanceof Player)) {
			return true;
		}
		if(!ConfigurationService.KIT_MAP) {
    		sender.sendMessage(CC.translate("&cThis command is only executable in KitMap."));
			return true;
		}
		Player player = (Player) sender;
		if (args.length <= 0) {
			this.getUsage(player, label);
			return true;
		}
		if (args[0].equalsIgnoreCase("setking")) {
			if (args.length < 2) {
				player.sendMessage(CC.translate("&cUsage: /" + label + " setking <player>"));
				return true;
			}
			Player king = Bukkit.getPlayer(args[1]);
			if (king == null) {
				player.sendMessage(CC.translate("&cPlayer isn't online."));
				return true;
			}
			KillTheKingManager.setKing(king);
			player.playSound(player.getLocation(), Sound.EXPLODE, 1F, 1F);
			player.sendMessage(CC.translate("&a" + KillTheKingManager.getKing().getName() + " is the new King."));
		}
		else if (args[0].equalsIgnoreCase("setprize")) {
			if (args.length < 2) {
				player.sendMessage(CC.translate("&cUsage: /" + label + " setprize <prize>"));
				return true;
			}
			String prize = args[1];
			KillTheKingManager.setPrize(prize);
			player.playSound(player.getLocation(), Sound.EXPLODE, 1F, 1F);
			player.sendMessage(CC.translate("&a" + KillTheKingManager.getPrize() + " is the new Prize."));
		}
		else if (args[0].equalsIgnoreCase("start")) {
			if (KillTheKingManager.getKing() == null) {
				player.sendMessage(CC.translate("&cPlease set a King. &7&o(/" + label + " setking <king>)"));
				return true;
			}
			if (KillTheKingManager.getPrize() == null) {
				player.sendMessage(CC.translate("&cPlease set a Prize. &7&o(/" + label + " setprize <prize>)"));
				return true;
			}
			if (KillTheKingManager.isActive()) {
				player.sendMessage(CC.translate("&cKill The King event is currently active."));
				return true;
			}
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
			}
			KillTheKingManager.setActive(true);
			KillTheKingManager.giveInventory(KillTheKingManager.getKing());
			KillTheKingManager.startMessage(KillTheKingManager.getKing());
			KillTheKingManager.getKing().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
			KillTheKingManager.getKing().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
		}
		else if (args[0].equalsIgnoreCase("stop")) {
			if (KillTheKingManager.getActive() == false) {
				player.sendMessage(CC.translate("&cKill The King event is not active."));
				return true;
			}
			for (Player online : Bukkit.getOnlinePlayers()) {
				online.playSound(online.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1F);
			}
			KillTheKingManager.clearInventory(KillTheKingManager.getKing());
			KillTheKingManager.setActive(false);
			KillTheKingManager.stopMessage();
		}
		else {
			this.getUsage(player, label);
		}
		return true;
	}
	
	public void getUsage(Player player, String label) {
		player.sendMessage(CC.translate(""));
		player.sendMessage(CC.translate("&6&lKillTheKing Commands"));
		player.sendMessage(CC.translate(""));
		player.sendMessage(CC.translate(" &e/" + label + " setking <player> &7- &fSet player King."));
		player.sendMessage(CC.translate(" &e/" + label + " setprize <prize> &7- &fSet Prize."));
		player.sendMessage(CC.translate(" &e/" + label + " start &7- &f Start the event."));
		player.sendMessage(CC.translate(" &e/" + label + " stop &7- &fStop the event."));
		player.sendMessage(CC.translate(""));
	}
}
