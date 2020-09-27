package net.bfcode.bfhcf.kothgame;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.citadel.EventSetCapzone;
import net.bfcode.bfhcf.kothgame.argument.EventCancelArgument;
import net.bfcode.bfhcf.kothgame.argument.EventCreateArgument;
import net.bfcode.bfhcf.kothgame.argument.EventDeleteArgument;
import net.bfcode.bfhcf.kothgame.argument.EventListArgument;
import net.bfcode.bfhcf.kothgame.argument.EventRenameArgument;
import net.bfcode.bfhcf.kothgame.argument.EventSetAreaArgument;
import net.bfcode.bfhcf.kothgame.argument.EventSetCapzoneArgument;
import net.bfcode.bfhcf.kothgame.argument.EventStartArgument;
import net.bfcode.bfhcf.kothgame.argument.EventUptimeArgument;

public class EventExecutor extends ArgumentExecutor {
	
    public EventExecutor(HCFaction plugin) {
        super("event");
        this.addArgument((CommandArgument)new EventListArgument(plugin));
        this.addArgument((CommandArgument)new EventCancelArgument(plugin));
        this.addArgument((CommandArgument)new EventCreateArgument(plugin));
        this.addArgument((CommandArgument)new EventDeleteArgument(plugin));
        this.addArgument((CommandArgument)new EventRenameArgument(plugin));
        this.addArgument((CommandArgument)new EventSetAreaArgument(plugin));
        this.addArgument((CommandArgument)new EventSetCapzoneArgument(plugin));
        this.addArgument((CommandArgument)new EventStartArgument(plugin));
        this.addArgument((CommandArgument)new EventUptimeArgument(plugin));
        this.addArgument((CommandArgument)new EventSetCapzone(plugin));
    }
}
