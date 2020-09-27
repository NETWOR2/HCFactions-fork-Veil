package net.bfcode.bfhcf.scoreboard;

import org.bukkit.command.CommandSender;

import java.util.Collections;

import net.bfcode.bfbase.command.module.essential.StaffModeCommand;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.classes.type.ArcherClass;
import net.bfcode.bfhcf.faction.struct.Relation;
import net.bfcode.bfhcf.faction.type.PlayerFaction;
import net.bfcode.bfhcf.utils.ConfigurationService;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.potion.PotionEffectType;
import java.util.Collection;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerBoard
{
    public static boolean NAMES_ENABLED;
    public static boolean INVISIBILITYFIX;
    public BufferedObjective bufferedObjective;
    private Team members;
    private Team archers;
    private Team neutrals;
    private Team allies;
    private Team focused;
    private Team staff;
    private Scoreboard scoreboard;
    private Player player;
    private HCFaction plugin;
    private boolean sidebarVisible;
    private boolean removed;
    private SidebarProvider defaultProvider;
    
    public PlayerBoard(HCFaction plugin, Player player) {
        this.sidebarVisible = false;
        this.removed = false;
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        (this.members = this.scoreboard.registerNewTeam("members")).setPrefix(ConfigurationService.TEAMMATE_COLOUR.toString());
        this.members.setCanSeeFriendlyInvisibles(true);
        (this.archers = this.scoreboard.registerNewTeam("archers")).setPrefix(ChatColor.DARK_RED.toString());
        (this.neutrals = this.scoreboard.registerNewTeam("neutrals")).setPrefix(ConfigurationService.ENEMY_COLOUR.toString());
        (this.allies = this.scoreboard.registerNewTeam("allies")).setPrefix(ConfigurationService.ALLY_COLOUR.toString());
        (this.focused = this.scoreboard.registerNewTeam("focused")).setPrefix(ChatColor.LIGHT_PURPLE.toString());
        (this.staff = this.scoreboard.registerNewTeam("staff")).setPrefix("§b[S] ");
        player.setScoreboard(this.scoreboard);
    }
    
    public void remove() {
        this.removed = true;
        if (this.scoreboard != null) {
            synchronized (this.scoreboard) {
                for (Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for (Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
            }
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public boolean isSidebarVisible() {
        return this.sidebarVisible;
    }
    
    public void setSidebarVisible(boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }
    
    public void setDefaultSidebar(SidebarProvider provider) {
        if (provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        if ((this.defaultProvider = provider) == null) {
            synchronized (this.scoreboard) {
                this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            }
        }
    }
    
    protected void updateObjective(long now) {
        synchronized (this.scoreboard) {
            SidebarProvider provider = this.defaultProvider;
            if (provider == null) {
                this.bufferedObjective.setVisible(false);
            }
            else {
                this.bufferedObjective.setTitle(provider.getTitle());
                this.bufferedObjective.setAllLines(provider.getLines(this.player));
                this.bufferedObjective.flip();
            }
        }
    }
    
    public boolean isRemoved() {
        return this.removed;
    }
    
    public SidebarProvider getDefaultProvider() {
        return this.defaultProvider;
    }
    
    private void setArcherTagged(Collection<Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        synchronized (this.scoreboard) {
            for (Player player : players) {
                if (!this.checkInvis(player)) {
                    this.archers.addPlayer(player);
                }
            }
        }
    }
    
    public void setMembers(Collection<Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        synchronized (this.scoreboard) {
            for (Player player : players) {
                this.members.addPlayer(player);
            }
        }
    }
    
    public void setAllies(Collection<Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        synchronized (this.scoreboard) {
            for (Player player : players) {
                if (!this.checkInvis(player)) {
                    this.allies.addPlayer(player);
                }
            }
        }
    }
    
    public void setNeutrals(Collection<Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        synchronized (this.scoreboard) {
            for (Player player : players) {
                if (!this.checkInvis(player)) {
                    this.neutrals.addPlayer(player);
                }
            }
        }
    }
    
    public boolean checkInvis(Player player) {
        return PlayerBoard.INVISIBILITYFIX && player.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }
    
    public void setFocused(Collection<Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        synchronized (this.scoreboard) {
            for (Player player : players) {
                if (!this.checkInvis(player)) {
                    this.focused.addPlayer(player);
                }
            }
        }
    }
    
    public void removeAll(Player player) {
        synchronized (this.scoreboard) {
            this.neutrals.removePlayer(player);
            this.allies.removePlayer(player);
            this.archers.removePlayer(player);
            this.focused.removePlayer(player);
            this.staff.removePlayer(player);
        }
        ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket((Packet)PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)player).getHandle()));
    }
    
    public void setStaff(Collection<Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        synchronized (this.scoreboard) {
            for (Player player : players) {
            	this.staff.addPlayer(player);
            }
        }
    }
    
    public void wipe(String entry) {
        synchronized (this.scoreboard) {
            this.neutrals.removeEntry(entry);
            this.members.removeEntry(entry);
            this.focused.removeEntry(entry);
            this.archers.removeEntry(entry);
            this.staff.removeEntry(entry);
            this.allies.removeEntry(entry);
        }
    }
    
    public void init(Player player) {
        this.init(Collections.singleton(player));
    }
    
    public void init(Collection<? extends Player> players) {
        if (!PlayerBoard.NAMES_ENABLED || this.isRemoved()) {
            return;
        }
        boolean foundFaction = false;
        PlayerFaction playerFaction = null;
        for (Player player : players) {
            this.wipe(player.getName());
            boolean invis = PlayerBoard.INVISIBILITYFIX && player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            if (player == this.player) {
                this.setMembers(Collections.singleton(player));
            }
            else if(StaffModeCommand.isMod(player)) {
            	this.setStaff(Collections.singleton(player));
            }
            else if (ArcherClass.tagged.containsKey(player.getUniqueId()) && !invis) {
                this.setArcherTagged(Collections.singleton(player));
            }
            else {
                if (!foundFaction) {
                    playerFaction = this.plugin.getFactionManager().getPlayerFaction(this.player);
                    foundFaction = true;
                }
                if (playerFaction != null) {
                    if (playerFaction.getMembers().keySet().contains(player.getUniqueId())) {
                        this.setMembers(Collections.singleton(player));
                    }
                    else if (invis) {
                        this.removeAll(player);
                    }
                    else if (playerFaction.getRelation((CommandSender)player) == Relation.ALLY) {
                        this.setAllies(Collections.singleton(player));
                    }
                    else if (playerFaction.getFocused() != null) {
                        if (!playerFaction.getFocused().equals(player.getUniqueId().toString())) {
                            continue;
                        }
                        this.setFocused(Collections.singleton(player));
                    }
                    else {
                        this.setNeutrals(Collections.singleton(player));
                    }
                }
                else if (invis) {
                    this.removeAll(player);
                }
                else {
                    this.setNeutrals(Collections.singleton(player));
                }
            }
        }
    }
    
    static {
        PlayerBoard.NAMES_ENABLED = true;
        PlayerBoard.INVISIBILITYFIX = true;
    }
}
