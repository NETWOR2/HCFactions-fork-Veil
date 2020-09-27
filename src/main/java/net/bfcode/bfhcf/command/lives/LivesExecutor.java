package net.bfcode.bfhcf.command.lives;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.command.lives.argument.LivesCheckArgument;
import net.bfcode.bfhcf.command.lives.argument.LivesClearDeathbansArgument;
import net.bfcode.bfhcf.command.lives.argument.LivesGiveArgument;
import net.bfcode.bfhcf.command.lives.argument.LivesReviveArgument;
import net.bfcode.bfhcf.command.lives.argument.LivesSetArgument;
import net.bfcode.bfhcf.command.lives.argument.LivesSetDeathbanTimeArgument;

public class LivesExecutor extends ArgumentExecutor
{
    public LivesExecutor(HCFaction plugin) {
        super("lives");
        this.addArgument(new LivesCheckArgument(plugin));
        this.addArgument(new LivesClearDeathbansArgument(plugin));
        this.addArgument(new LivesGiveArgument(plugin));
        this.addArgument(new LivesReviveArgument(plugin));
        this.addArgument(new LivesSetArgument(plugin));
        this.addArgument(new LivesSetDeathbanTimeArgument());
    }
}
