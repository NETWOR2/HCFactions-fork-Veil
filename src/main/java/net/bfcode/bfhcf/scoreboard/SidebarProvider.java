package net.bfcode.bfhcf.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;

public interface SidebarProvider
{
    String getTitle();
    
    List<SidebarEntry> getLines(Player p0);
}
