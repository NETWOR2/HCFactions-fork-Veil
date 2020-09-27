package net.bfcode.bfhcf.kothgame.koth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.kothgame.koth.argument.KothNextArgument;
import net.bfcode.bfhcf.kothgame.koth.argument.KothScheduleArgument;
import net.bfcode.bfhcf.kothgame.koth.argument.KothSetCapDelayArgument;

public class KothExecutor extends ArgumentExecutor
{
    private KothScheduleArgument kothScheduleArgument;
    
    public KothExecutor(HCFaction plugin) {
        super("koth");
        this.addArgument((CommandArgument)new KothNextArgument(plugin));
        this.addArgument((CommandArgument)(this.kothScheduleArgument = new KothScheduleArgument(plugin)));
        this.addArgument((CommandArgument)new KothSetCapDelayArgument(plugin));
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
}
