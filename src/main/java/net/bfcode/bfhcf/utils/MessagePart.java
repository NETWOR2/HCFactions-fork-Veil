package net.bfcode.bfhcf.utils;

import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.ChatColor;

class MessagePart {
	
    public ChatColor color;
    
    public ChatColor[] styles;
    
    public String clickActionName;
    
    public String clickActionData;
    
    public String hoverActionName;
    
    public String hoverActionData;
    
    public String text;
    
    public MessagePart(String text) {
        this.text = text;
    }
    
    public JsonWriter writeJson(JsonWriter json) {
        try {
            json.beginObject().name("text").value(this.text);
            if (this.color != null) {
                json.name("color").value(this.color.name().toLowerCase());
            }
            if (this.styles != null) {
                ChatColor[] arrayOfChatColor;
                for (int j = (arrayOfChatColor = this.styles).length, i = 0; i < j; ++i) {
                    ChatColor style = arrayOfChatColor[i];
                    json.name(style.name().toLowerCase()).value(true);
                }
            }
            if (this.clickActionName != null && this.clickActionData != null) {
                json.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value").value(this.clickActionData).endObject();
            }
            if (this.hoverActionName != null && this.hoverActionData != null) {
                json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value").value(this.hoverActionData).endObject();
            }
            return json.endObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return json;
        }
    }
}
