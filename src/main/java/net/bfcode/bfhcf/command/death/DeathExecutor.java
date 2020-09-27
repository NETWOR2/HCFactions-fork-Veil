package net.bfcode.bfhcf.command.death;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.command.death.argument.DeathHistoryArgument;
import net.bfcode.bfhcf.command.death.argument.DeathInfoArgument;
import net.bfcode.bfhcf.command.death.argument.DeathRefundArgument;
import net.bfcode.bfhcf.command.death.argument.DeathReviveArgument;

public class DeathExecutor extends ArgumentExecutor
{
    public DeathExecutor(HCFaction plugin) {
        super("death");
        this.addArgument((CommandArgument)new DeathInfoArgument(plugin));
        this.addArgument((CommandArgument)new DeathRefundArgument(plugin));
        this.addArgument((CommandArgument)new DeathReviveArgument(plugin));
        this.addArgument((CommandArgument)new DeathHistoryArgument(plugin));
    }
}
