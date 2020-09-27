package net.bfcode.bfhcf.faction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.bfcode.bfbase.util.command.ArgumentExecutor;
import net.bfcode.bfbase.util.command.CommandArgument;
import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.faction.argument.FactionAcceptArgument;
import net.bfcode.bfhcf.faction.argument.FactionAlertArgument;
import net.bfcode.bfhcf.faction.argument.FactionAllyArgument;
import net.bfcode.bfhcf.faction.argument.FactionAnnouncementArgument;
import net.bfcode.bfhcf.faction.argument.FactionChatArgument;
import net.bfcode.bfhcf.faction.argument.FactionClaimArgument;
import net.bfcode.bfhcf.faction.argument.FactionClaimChunkArgument;
import net.bfcode.bfhcf.faction.argument.FactionClaimsArgument;
import net.bfcode.bfhcf.faction.argument.FactionCoLeaderArgument;
import net.bfcode.bfhcf.faction.argument.FactionCreateArgument;
import net.bfcode.bfhcf.faction.argument.FactionDemoteArgument;
import net.bfcode.bfhcf.faction.argument.FactionDepositArgument;
import net.bfcode.bfhcf.faction.argument.FactionDisbandArgument;
import net.bfcode.bfhcf.faction.argument.FactionFriendlyFireArgument;
import net.bfcode.bfhcf.faction.argument.FactionHelpArgument;
import net.bfcode.bfhcf.faction.argument.FactionHomeArgument;
import net.bfcode.bfhcf.faction.argument.FactionInviteArgument;
import net.bfcode.bfhcf.faction.argument.FactionInvitesArgument;
import net.bfcode.bfhcf.faction.argument.FactionKickArgument;
import net.bfcode.bfhcf.faction.argument.FactionLeaderArgument;
import net.bfcode.bfhcf.faction.argument.FactionLeaveArgument;
import net.bfcode.bfhcf.faction.argument.FactionListArgument;
import net.bfcode.bfhcf.faction.argument.FactionMapArgument;
import net.bfcode.bfhcf.faction.argument.FactionMessageArgument;
import net.bfcode.bfhcf.faction.argument.FactionOpenArgument;
import net.bfcode.bfhcf.faction.argument.FactionPointsCommand;
import net.bfcode.bfhcf.faction.argument.FactionPromoteArgument;
import net.bfcode.bfhcf.faction.argument.FactionRallyArgument;
import net.bfcode.bfhcf.faction.argument.FactionRenameArgument;
import net.bfcode.bfhcf.faction.argument.FactionSetHomeArgument;
import net.bfcode.bfhcf.faction.argument.FactionShowArgument;
import net.bfcode.bfhcf.faction.argument.FactionStuckArgument;
import net.bfcode.bfhcf.faction.argument.FactionUnallyArgument;
import net.bfcode.bfhcf.faction.argument.FactionUnclaimArgument;
import net.bfcode.bfhcf.faction.argument.FactionUninviteArgument;
import net.bfcode.bfhcf.faction.argument.FactionWithdrawArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionBanArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionChatSpyArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionClaimForArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionClearClaimsArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionForceJoinArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionForceKickArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionForceLeaderArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionForcePromoteArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionLockArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionMuteArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionRemoveArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionRemovePointsArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionSetDeathbanMultiplierArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionSetDtrArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionSetDtrRegenArgument;
import net.bfcode.bfhcf.faction.argument.staff.FactionSetPointsArgument;

public class FactionExecutor extends ArgumentExecutor
{
    private CommandArgument helpArgument;
    
    public FactionExecutor(HCFaction plugin) {
        super("faction");
        this.addArgument(new FactionAcceptArgument(plugin));
        this.addArgument(new FactionAllyArgument(plugin));
        this.addArgument(new FactionChatArgument(plugin));
        this.addArgument(new FactionChatSpyArgument(plugin));
        this.addArgument(new FactionClaimArgument(plugin));
        this.addArgument(new FactionClaimChunkArgument(plugin));
        this.addArgument(new FactionClaimForArgument(plugin));
        this.addArgument(new FactionClaimsArgument(plugin));
        this.addArgument(new FactionClearClaimsArgument(plugin));
        this.addArgument(new FactionCreateArgument(plugin));
        this.addArgument(new FactionAnnouncementArgument(plugin));
        this.addArgument(new FactionDemoteArgument(plugin));
        this.addArgument(new FactionCoLeaderArgument(plugin));
        this.addArgument(new FactionDepositArgument(plugin));
        this.addArgument(new FactionDisbandArgument(plugin));
        this.addArgument(new FactionLockArgument(plugin));
        this.addArgument(new FactionSetDtrRegenArgument(plugin));
        this.addArgument(new FactionForceJoinArgument(plugin));
        this.addArgument(new FactionForceKickArgument(plugin));
        this.addArgument(new FactionForceLeaderArgument(plugin));
        this.addArgument(new FactionForcePromoteArgument(plugin));
        this.addArgument(new FactionBanArgument(plugin));
        this.addArgument(this.helpArgument = new FactionHelpArgument(this));
        this.addArgument(new FactionMuteArgument(plugin));
        this.addArgument(new FactionHomeArgument(this, plugin));
        this.addArgument(new FactionInviteArgument(plugin));
        this.addArgument(new FactionInvitesArgument(plugin));
        this.addArgument(new FactionKickArgument(plugin));
        this.addArgument(new FactionLeaderArgument(plugin));
        this.addArgument(new FactionLeaveArgument(plugin));
        this.addArgument(new FactionListArgument(plugin));
        this.addArgument(new FactionMapArgument(plugin));
        this.addArgument(new FactionMessageArgument(plugin));
        this.addArgument(new FactionOpenArgument(plugin));
        this.addArgument(new FactionRemoveArgument(plugin));
        this.addArgument(new FactionRenameArgument(plugin));
        this.addArgument(new FactionPromoteArgument(plugin));
        this.addArgument(new FactionSetDtrArgument(plugin));
        this.addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        this.addArgument(new FactionSetHomeArgument(plugin));
        this.addArgument(new FactionShowArgument(plugin));
        this.addArgument(new FactionStuckArgument(plugin));
        this.addArgument(new FactionUnclaimArgument(plugin));
        this.addArgument(new FactionUnallyArgument(plugin));
        this.addArgument(new FactionUninviteArgument(plugin));
        this.addArgument(new FactionWithdrawArgument(plugin));
        this.addArgument(new FactionPointsCommand(plugin));
        this.addArgument(new FactionSetPointsArgument(plugin));
        this.addArgument(new FactionRemovePointsArgument());
        this.addArgument(new FactionAlertArgument(plugin));
        this.addArgument(new FactionRallyArgument(plugin));
        this.addArgument(new FactionFriendlyFireArgument(plugin));
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }
        CommandArgument argument = this.getArgument(args[0]);
        String permission;
        if (argument != null && ((permission = argument.getPermission()) == null || sender.hasPermission(permission))) {
            argument.onCommand(sender, command, label, args);
            return true;
        }
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}
