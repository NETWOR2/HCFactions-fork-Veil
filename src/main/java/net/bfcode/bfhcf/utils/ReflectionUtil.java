package net.bfcode.bfhcf.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;

public class ReflectionUtil {
	
    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = String.valueOf(name.substring(name.lastIndexOf(46) + 1)) + ".";
        return version;
    }
    
    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }
    
    public static Class<?> getOBCClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }
    
    public static Object getHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle", (Class<?>[])new Class[0]).invoke(obj, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        Method[] arrayOfMethod;
        for (int j = (arrayOfMethod = clazz.getMethods()).length, i = 0; i < j; ++i) {
            Method m = arrayOfMethod[i];
            if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }
    
    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length) {
            return false;
        }
        for (int i = 0; i < l1.length; ++i) {
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        }
        return equal;
    }
}
