package net.bfcode.bfhcf.faction.struct;

public enum Role
{
    LEADER("Leader", "***"), 
    COLEADER("CoLeader", "**"),
    CAPTAIN("Captain", "*"), 
    MEMBER("Member", "");
    
    private String name;
    private String astrix;
    
    private Role(String name, String astrix) {
        this.name = name;
        this.astrix = astrix;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAstrix() {
        return this.astrix;
    }
}
