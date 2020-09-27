package net.bfcode.bfhcf.faction.argument;

import java.text.DecimalFormat;

import dev.hatsur.library.Library;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.util.CC;
import net.bfcode.bfbase.util.Cooldowns;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.struct.Role;
import net.bfcode.bfhcf.faction.type.PlayerFaction;

public class FactionRallyArgument extends CommandArgument {
	
	private HCFaction plugin;
	public String coords;
	
	public FactionRallyArgument(HCFaction plugin) {
		super("rally", "send your coords in faction scoreboard");
		this.plugin = plugin;
	}
    
    public String getUsage(String label) {
        return "/" + label;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if(Cooldowns.isOnCooldown("TEMPCOORDS_COMMAND", player)) {
        	sender.sendMessage(CC.translate("&cTienes que esperar " + Cooldowns.getCooldownInt("TEMPCOORDS_COMMAND", player) + " Segundos para volver a colocar este comando."));
        	return true;
        }
        if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
        	sender.sendMessage(CC.translate("&cSolo el Leader y Captain puede ejecutar estos comandos."));
        	return true;
        }
        for(Player players : playerFaction.getOnlinePlayers()) {
            Cooldowns.addCooldown("TEMPORAL_COORDS", players, 180);
            Cooldowns.addCooldown("TEMPCOORDS_COMMAND", players, 60);
            players.sendMessage("");
            players.sendMessage(CC.translate(Library.getInstance().getPermissionManager().getCurrentPermissionAdapter().getProfile(player.getUniqueId()).getColoredUsername() + " &ffijo un punto para reagruparse"));
            players.sendMessage("");
        }
        DecimalFormat df = new DecimalFormat("#");
        playerFaction.setTemporalCoords(df.format(player.getLocation().getX()) + ", " + df.format(player.getLocation().getZ()));
        sender.sendMessage(CC.translate("&eAcaban de enviar las coords de un usuario de tu faction, tendran que esperar 1 Minuto!"));
        return true;
    }
}
