package net.bfcode.bfhcf.kothgame.conquest;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;

public class ConquestExecutor extends ArgumentExecutor
{
    public ConquestExecutor(HCFaction plugin) {
        super("conquest");
        this.addArgument((CommandArgument)new ConquestSetpointsArgument(plugin));
    }
}
