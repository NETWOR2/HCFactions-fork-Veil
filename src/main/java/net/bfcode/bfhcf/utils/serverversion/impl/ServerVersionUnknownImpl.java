package net.bfcode.bfhcf.utils.serverversion.impl;

import net.bfcode.bfhcf.utils.serverversion.IServerVersion;
import org.bukkit.entity.Player;

public class ServerVersionUnknownImpl implements IServerVersion {

    @Override
    public void clearArrowsFromPlayer(Player player) {

    }

    @Override
    public String getPlayerLanguage(Player player) {
        return "en";
    }
}
