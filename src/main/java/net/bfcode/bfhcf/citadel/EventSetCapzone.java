package net.bfcode.bfhcf.citadel;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import java.util.Collection;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfbase.util.cuboid.Cuboid;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.FactionManager;
import net.bfcode.bfhcf.faction.claim.Claim;
import net.bfcode.bfhcf.faction.type.Faction;
import net.bfcode.bfhcf.kothgame.CaptureZone;
import net.bfcode.bfhcf.kothgame.faction.CapturableFaction;
import net.bfcode.bfhcf.kothgame.faction.ConquestFaction;
import net.bfcode.bfhcf.kothgame.faction.EventFaction;
import net.bfcode.bfhcf.kothgame.tracker.ConquestTracker;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventSetCapzone extends CommandArgument
{
    private HCFaction plugin;
    
    public EventSetCapzone(HCFaction plugin) {
        super("setcitadelzone", "Sets the capture zone of an event");
        this.plugin = plugin;
        this.aliases = new String[] { "setcapturezone", "setcap", "setcappoint", "setcapturepoint", "setcappoint" };
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " ";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set KOTH arena capture points");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
        if (worldEdit == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set KOTH capture points.");
            return true;
        }
        Selection selection = worldEdit.getSelection((Player)sender);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if (selection.getWidth() < 2 || selection.getLength() < 2) {
            sender.sendMessage(ChatColor.RED + "Capture zones must be at least " + 2 + 'x' + 2 + '.');
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof CapturableFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not a capturable faction named '" + args[1] + "'.");
            return true;
        }
        CapturableFaction capturableFaction = (CapturableFaction)faction;
        Collection<Claim> claims = capturableFaction.getClaims();
        if (claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
            return true;
        }
        Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
        World world = claim.getWorld();
        int minimumX = claim.getMinimumX();
        int maximumX = claim.getMaximumX();
        int minimumZ = claim.getMinimumZ();
        int maximumZ = claim.getMaximumZ();
        FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                Faction factionAt = factionManager.getFactionAt(world, x, z);
                if (!factionAt.equals(capturableFaction)) {
                    sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
                    return true;
                }
            }
        }
        CaptureZone captureZone;
        if (capturableFaction instanceof ConquestFaction) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + this.getName() + ' ' + faction.getName() + " ");
                return true;
            }
            ConquestFaction conquestFaction = (ConquestFaction)capturableFaction;
            ConquestFaction.ConquestZone conquestZone = ConquestFaction.ConquestZone.getByName(args[2]);
            if (conquestZone == null) {
                sender.sendMessage(ChatColor.RED + "There is no conquest zone named '" + args[2] + "'.");
                sender.sendMessage(ChatColor.RED + "Did you mean?: " + StringUtils.join(ConquestFaction.ConquestZone.getNames(), ", "));
                return true;
            }
            captureZone = new CaptureZone(conquestZone.getName(), conquestZone.getColor().toString(), (Cuboid)claim, ConquestTracker.DEFAULT_CAP_MILLIS);
            conquestFaction.setZone(conquestZone, captureZone);
        }
        else {
            ((CitadelFaction)capturableFaction).setCaptureZone(captureZone = new CaptureZone(capturableFaction.getName(), (Cuboid)claim, CitadelTracker.DEFAULT_CAP_MILLIS1));
        }
        sender.sendMessage(ConfigurationService.BASECOLOUR + "Set capture zone " + captureZone.getDisplayName() + ConfigurationService.BASECOLOUR + " for faction " + faction.getName() + ConfigurationService.BASECOLOUR + '.');
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2: {
                return (List<String>) this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction);
            }
            case 3: {
                Faction faction2 = this.plugin.getFactionManager().getFaction(args[1]);
                if (faction2 instanceof ConquestFaction) {
                    ConquestFaction.ConquestZone[] zones = ConquestFaction.ConquestZone.values();
                    List<String> results = new ArrayList<String>(zones.length);
                    for (ConquestFaction.ConquestZone zone : zones) {
                        results.add(zone.name());
                    }
                    return results;
                }
                return Collections.emptyList();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
}
