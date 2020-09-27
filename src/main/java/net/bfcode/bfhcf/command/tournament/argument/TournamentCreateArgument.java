package net.bfcode.bfhcf.command.tournament.argument;

import dev.hatsur.library.Library;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfbase.listener.VanishListener;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.faction.type.SpawnFaction;
import net.bfcode.bfhcf.tournaments.Tournament;
import net.bfcode.bfhcf.tournaments.TournamentType;
import net.bfcode.bfhcf.tournaments.file.TournamentFile;
import net.bfcode.bfhcf.tournaments.runnable.TournamentRunnable;
import net.bfcode.bfhcf.utils.CC;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.bfcode.bfhcf.utils.Cooldowns;
import net.bfcode.bfhcf.utils.FancyMessage;
import net.bfcode.bfhcf.utils.Messager;
import net.bfcode.bfhcf.utils.command.CommandArgument;
import net.minecraft.util.com.google.common.primitives.Ints;

public class TournamentCreateArgument extends CommandArgument {
	
    private HCFaction plugin;
    @SuppressWarnings("unused")
	private String sender;
    
    public TournamentCreateArgument() {
        super("create", "Create a tournament");
        this.plugin = HCFaction.getPlugin();
        this.permission = "hcf.command.tournament.argument.create";
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <maxplayers> <sumo|diamond|bard|assassin|bomber|axe|archer|spleef>";
    }
    
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cYou must be player to execute this command."));
            return true;
        }
        Player player = (Player)sender;
        for(ItemStack item : player.getInventory().getContents()) {
		    if (item != null) {
		    	player.sendMessage(CC.translate("&cYou need to empty your inventory to create the event!"));
		    	return true;
		    }
		}
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if(!(factionAt instanceof SpawnFaction)) {
        	player.sendMessage(CC.translate("&cYou only create events Spawn Zone!"));
        	return true;
        }
        if (Cooldowns.isOnCooldown("TOURNAMENT_COOLDOWN", player) && !player.isOp() && !player.hasPermission("tournament.cooldown.bypass")) {
            player.sendMessage("§cWait §l" + (Cooldowns.getCooldownForPlayerInt("TOURNAMENT_COOLDOWN", player) / 60) + " §cminutes §cfor create event!");
            return true;
        }
        if(StaffModeCommand.isMod(player) || VanishListener.isVanished(player)) {
        	player.sendMessage(CC.translate("&cYou cant execute this command in StaffMode"));
        	return true;
        }
        if(!ConfigurationService.KIT_MAP) {
        	Messager.player(player, CC.translate("&cEvents only create on KitMap!"));
        	return true;
        }
        if(HCFaction.getPlugin().getSotwTimer().getSotwRunnable() != null) {
        	player.sendMessage(CC.translate("&cYou cant execute this command in SOTW"));
        	return true;
        }
        if (args.length < 3) {
            player.sendMessage(CC.translate("&cUsage: " + this.getUsage(label)));
        }
        else {
            if (this.plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
                player.sendMessage(CC.translate("&cYou cant host events in Combat Tag!"));
                return true;
            }
            Integer size = Ints.tryParse(args[1]);
            if (size == null || size < 1) {
                player.sendMessage(CC.translate("&cInvalid size."));
                return true;
            }
            if (size == null || size > 100) {
                player.sendMessage(CC.translate("&cMaximun size is 100."));
                return true;
            }
            if (this.plugin.getTournamentManager().isCreated()) {
                player.sendMessage(CC.translate("&cThis event is already created!"));
                return true;
            }
            this.sender = player.getDisplayName();
            TournamentType type = TournamentType.valueOf(args[2].toUpperCase());
            if(TournamentFile.getConfig().getString("Locations." + args[2] + ".Spawn") == null) {
            	sender.sendMessage(CC.translate("&cThis event does not have the locations created!"));
            	return true;
            }
            this.plugin.getTournamentManager().createTournament(player, size, type, player);
            player.performCommand("tournament join");
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            	String name = Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getColoredUsername();
                Tournament tournament = HCFaction.getPlugin().getTournamentManager().getTournament();
                FancyMessage message = new FancyMessage("");
                online.sendMessage("");
                message.then(CC.translate("&7(&4&lEvent&7) &4" + type.getName() + "&c Hosted by " + name + " &7(&c" + tournament.getPlayers().size() + " &7/&c " + tournament.getSize() + "&7)")).then(CC.translate(" &4¡Click here!")).tooltip(CC.translate("&a¡Right Click for join!")).command("/tournament join").send(online);
                online.sendMessage("");
            }
            new TournamentRunnable(this.plugin.getTournamentManager().getTournament()).runAnnounce();
            Cooldowns.addCooldown("TOURNAMENT_COOLDOWN", player, 1800);
            }
        return true;
    }
}
