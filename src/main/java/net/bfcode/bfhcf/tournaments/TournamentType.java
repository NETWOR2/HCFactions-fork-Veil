package net.bfcode.bfhcf.tournaments;

public enum TournamentType {
	
	SUMO("Sumo"), 
	DIAMOND("DIAMOND"),
	BARD("BARD"),
	ASSASSIN("ASSASSIN"),
	BOMBER("BOMBER"),
	AXE("Axe"),
	ARCHER("Archer"),
	SPLEEF("Spleef"),
	TNTTAG("TNTTag");
    
    private String name;
    
    private TournamentType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
