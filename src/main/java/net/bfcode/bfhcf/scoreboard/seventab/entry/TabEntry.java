package net.bfcode.bfhcf.scoreboard.seventab.entry;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.bfcode.bfhcf.scoreboard.seventab.skin.Skin;

@Setter @Accessors(chain = true) 
@Getter
public class TabEntry {

	private int column;
    private int row;
    
	private String text;
    
    private int ping = 0;
    private Skin skin = Skin.DEFAULT_SKIN;

    public TabEntry(int column, int row, String text) {
        this.column = column;
        this.row = row;
        this.text = text;
    }
}