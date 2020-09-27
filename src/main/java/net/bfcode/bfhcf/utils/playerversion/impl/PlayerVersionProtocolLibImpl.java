package net.bfcode.bfhcf.utils.playerversion.impl;

import com.comphenix.protocol.ProtocolLibrary;

import net.bfcode.bfhcf.utils.playerversion.IPlayerVersion;
import net.bfcode.bfhcf.utils.playerversion.PlayerVersion;
import org.bukkit.entity.Player;

public class PlayerVersionProtocolLibImpl implements IPlayerVersion {

    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(
                ProtocolLibrary.getProtocolManager().getProtocolVersion(player)
        );
    }
}
