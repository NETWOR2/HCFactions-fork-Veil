package net.bfcode.bfhcf.utils.serverversion;

import net.bfcode.bfhcf.utils.serverversion.impl.ServerVersion1_8_R3Impl;
import net.bfcode.bfhcf.utils.serverversion.impl.ServerVersionUnknownImpl;
import org.bukkit.Bukkit;

public class ServerVersionHandler {

    public static IServerVersion version;
    public static String serverVersionName;

    public ServerVersionHandler() {
        serverVersionName = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        switch (serverVersionName) {
            case "v1_8_R3":
                version = new ServerVersion1_8_R3Impl();
                break;
            default:
                version = new ServerVersionUnknownImpl();
                break;
        }
    }
}
