package net.bfcode.bfhcf.kothgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.bfcode.bfhcf.HCFaction;
import net.bfcode.bfhcf.utils.JavaUtil;

public class EventScheduler {
	
    private static long QUERY_DELAY;
    private Map<LocalDateTime, String> scheduleMap;
    private HCFaction plugin;
    private long lastQuery;
    
    public EventScheduler(HCFaction plugin) {
        this.scheduleMap = new LinkedHashMap<LocalDateTime, String>();
        this.plugin = plugin;
        this.reloadSchedules();
    }
    
    private static LocalDateTime getFromString(String input) {
        if (!input.contains(",")) {
            return null;
        }
        String[] args = input.split(",");
        if (args.length != 5) {
            return null;
        }
        Integer year = JavaUtil.tryParseInt(args[0]);
        if (year == null) {
            return null;
        }
        Integer month = JavaUtil.tryParseInt(args[1]);
        if (month == null) {
            return null;
        }
        Integer day = JavaUtil.tryParseInt(args[2]);
        if (day == null) {
            return null;
        }
        Integer hour = JavaUtil.tryParseInt(args[3]);
        if (hour == null) {
            return null;
        }
        Integer minute = JavaUtil.tryParseInt(args[4]);
        if (minute == null) {
            return null;
        }
        return LocalDateTime.of(year, month, day, hour, minute);
    }
    
    private void reloadSchedules() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.plugin.getDataFolder(), "event-schedules.txt")), StandardCharsets.UTF_8))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String[] args;
                if (!currentLine.startsWith("#") && (args = (currentLine = currentLine.trim()).split(":")).length == 2) {
                    LocalDateTime localDateTime;
                    if ((localDateTime = getFromString(args[0])) == null) {
                        continue;
                    }
                    this.scheduleMap.put(localDateTime, args[1]);
                }
            }
        }
        catch (FileNotFoundException ex2) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not find file event-schedules.txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public Map<LocalDateTime, String> getScheduleMap() {
        long millis = System.currentTimeMillis();
        if (millis - EventScheduler.QUERY_DELAY > this.lastQuery) {
            this.reloadSchedules();
            this.lastQuery = millis;
        }
        return this.scheduleMap;
    }
    
    static {
        QUERY_DELAY = TimeUnit.SECONDS.toMillis(60L);
    }
}
