package net.bfcode.bfhcf.command.tournament;

import net.bfcode.bfhcf.command.tournament.argument.TournamentCancelArgument;
import net.bfcode.bfhcf.command.tournament.argument.TournamentCreateArgument;
import net.bfcode.bfhcf.command.tournament.argument.TournamentJoinArgument;
import net.bfcode.bfhcf.command.tournament.argument.TournamentLeaveArgument;
import net.bfcode.bfhcf.command.tournament.argument.TournamentSetArgument;
import net.bfcode.bfhcf.command.tournament.argument.TournamentStatusArgument;
import net.bfcode.bfhcf.utils.command.ArgumentExecutor;
import net.bfcode.bfhcf.utils.command.CommandArgument;

public class TournamentExecutor extends ArgumentExecutor {

	public TournamentExecutor() {
		super("tournament");
		this.addArgument((CommandArgument)new TournamentCreateArgument());
        this.addArgument((CommandArgument)new TournamentCancelArgument());
        this.addArgument((CommandArgument)new TournamentStatusArgument());
        this.addArgument((CommandArgument)new TournamentJoinArgument());
        this.addArgument((CommandArgument)new TournamentLeaveArgument());
        this.addArgument((CommandArgument)new TournamentSetArgument());
	}

}
