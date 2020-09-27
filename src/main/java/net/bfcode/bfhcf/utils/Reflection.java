package net.bfcode.bfhcf.utils;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class Reflection
{
    private String name;
    private String version;
    private DecimalFormat format;
    private Object serverInstance;
    private Field tpsField;
    
    public Reflection() {
        this.name = Bukkit.getServer().getClass().getPackage().getName();
        this.version = this.name.substring(this.name.lastIndexOf(46) + 1);
        this.format = new DecimalFormat("##.##");
    }
    
    private Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + this.version + "." + className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void getTpsRun() {
        try {
            this.serverInstance = this.getNMSClass("MinecraftServer").getMethod("getServer", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            this.tpsField = this.serverInstance.getClass().getField("recentTps");
        }
        catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex2) {
            Exception ex = null;
            Exception e = ex;
            e.printStackTrace();
        }
    }
    
    public String getTPS(int time) {
        try {
            double[] tps = (double[])this.tpsField.get(this.serverInstance);
            return this.format.format(tps[time]);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Long getLag() {
        return Math.round((1.0 - Double.parseDouble(this.getTPS(0).replace(",", ".")) / 20.0) * 100.0);
    }
}
