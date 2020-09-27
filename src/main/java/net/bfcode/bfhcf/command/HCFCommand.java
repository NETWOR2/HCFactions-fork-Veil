package net.bfcode.bfhcf.command;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bfcode.bfbase.BaseConstants;
import net.bfcode.bfbase.util.BukkitUtils;
import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;

public class HCFCommand extends ArgumentExecutor
{
    private static Pattern USAGE_REPLACER_PATTERN;
    private String name;
    private String description;
    private String[] aliases;
    private String usage;
    
    public HCFCommand(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }
    
    public static boolean checkNull(CommandSender sender, String player) {
        Player target = BukkitUtils.playerWithNameOrUUID(player);
        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, player));
            return true;
        }
        return false;
    }
    
    public static boolean canSee(CommandSender sender, Player target) {
        return target != null && (!(sender instanceof Player) || ((Player)sender).canSee(target));
    }
    
    public String getPermission() {
        return "command." + this.name;
    }
    
    public boolean isPlayerOnlyCommand() {
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getUsage() {
        if (this.usage == null) {
            this.usage = "";
        }
        return ChatColor.RED + "Usage: " + HCFCommand.USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(this.name) + ChatColor.GOLD + " - " + this.description;
    }
    
    public void setUsage(String usage) {
        this.usage = usage;
    }
    
    public String getUsage(String label) {
        return ChatColor.RED + "Usage: " + HCFCommand.USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(label) + ChatColor.GOLD + " - " + this.description;
    }
    
    public String[] getAliases() {
        if (this.aliases == null) {
            this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return Arrays.copyOf(this.aliases, this.aliases.length);
    }
    
    protected void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
    
    static {
        USAGE_REPLACER_PATTERN = Pattern.compile("(command)", 16);
    }
}
