package net.bfcode.bfhcf.user;

public enum TabStyles
{
    CLASSIC("Classic"), 
    FACTION_LIST("Faction List"), 
    STAFF("Staff Utils");
    
    private String toString;
    
    private TabStyles(String toString) {
        this.toString = toString;
    }
    
    @Override
    public String toString() {
        return this.toString;
    }
}
