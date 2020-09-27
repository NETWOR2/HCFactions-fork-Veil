package net.bfcode.bfhcf.utils;

public class Methods {
    
    public static boolean isInt(final String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
