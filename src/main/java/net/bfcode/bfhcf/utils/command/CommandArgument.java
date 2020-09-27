package net.bfcode.bfhcf.utils.command;

import org.apache.commons.lang.*;
import org.bukkit.command.*;

import net.bfcode.bfhcf.HCFaction;

import java.util.*;

public abstract class CommandArgument
{
    private String name;
    private String description;
    protected String permission;
    private String[] aliases;
    public HCFaction plugin;
    
    public CommandArgument(String name) {
        this(name, null, null);
    }
    
    public CommandArgument(String name, String description) {
        this(name, null, description);
    }
    
    public CommandArgument(String name, String permission, String description) {
        this(name, permission, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }
    
    public CommandArgument(String name, String permission, String description, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.aliases = Arrays.copyOf(aliases, aliases.length);
        this.plugin = HCFaction.getPlugin();
    }
    
    public String getPermission(PluginCommand command) {
        return (this.permission != null) ? this.permission : null;
    }
    
    public abstract String getUsage(String p0);
    
    public abstract boolean onCommand(CommandSender p0, Command p1, String p2, String[] p3);
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public String[] getAliases() {
        return this.aliases;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
}
