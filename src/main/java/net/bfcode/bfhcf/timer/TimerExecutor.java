package net.bfcode.bfhcf.timer;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.timer.argument.TimerCheckArgument;
import net.bfcode.bfhcf.timer.argument.TimerSetArgument;

public class TimerExecutor extends ArgumentExecutor
{
    public TimerExecutor(HCFaction plugin) {
        super("timer");
        this.addArgument((CommandArgument)new TimerCheckArgument(plugin));
        this.addArgument((CommandArgument)new TimerSetArgument(plugin));
    }
}
