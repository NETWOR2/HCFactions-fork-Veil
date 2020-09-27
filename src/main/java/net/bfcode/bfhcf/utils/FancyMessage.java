package net.bfcode.bfhcf.utils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FancyMessage {
	
    private List<MessagePart> messageParts;
    
    private String jsonString;
    
    private boolean dirty;
    
    private Class<?> nmsChatSerializer;
    
    private Class<?> nmsTagCompound;
    
    private Class<?> nmsPacketPlayOutChat;
    
    private Class<?> nmsAchievement;
    
    private Class<?> nmsStatistic;
    
    private Class<?> nmsItemStack;
    
    private Class<?> obcStatistic;
    
    private Class<?> obcItemStack;
    
    public FancyMessage(String firstPartText) {
        this.nmsChatSerializer = ReflectionUtil.getNMSClass("ChatSerializer");
        this.nmsTagCompound = ReflectionUtil.getNMSClass("NBTTagCompound");
        this.nmsPacketPlayOutChat = ReflectionUtil.getNMSClass("PacketPlayOutChat");
        this.nmsAchievement = ReflectionUtil.getNMSClass("Achievement");
        this.nmsStatistic = ReflectionUtil.getNMSClass("Statistic");
        this.nmsItemStack = ReflectionUtil.getNMSClass("ItemStack");
        this.obcStatistic = ReflectionUtil.getOBCClass("CraftStatistic");
        this.obcItemStack = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        (this.messageParts = new ArrayList<MessagePart>()).add(new MessagePart(firstPartText));
        this.jsonString = null;
        this.dirty = false;
    }
    
    public FancyMessage color(ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(String.valueOf(color.name()) + " is not a color");
        }
        this.latest().color = color;
        this.dirty = true;
        return this;
    }
    
    public FancyMessage style(ChatColor... styles) {
        for (ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(String.valueOf(style.name()) + " is not a style");
            }
        }
        this.latest().styles = styles;
        this.dirty = true;
        return this;
    }
    
    public FancyMessage file(String path) {
        this.onClick("open_file", path);
        return this;
    }
    
    public FancyMessage link(String url) {
        this.onClick("open_url", url);
        return this;
    }
    
    public FancyMessage suggest(String command) {
        this.onClick("suggest_command", command);
        return this;
    }
    
    public FancyMessage command(String command) {
        this.onClick("run_command", command);
        return this;
    }
    
    public FancyMessage achievementTooltip(String name) {
        this.onHover("show_achievement", "achievement." + name);
        return this;
    }
    
    public FancyMessage achievementTooltip(Achievement which) {
        try {
            Object achievement = ReflectionUtil.getMethod(this.obcStatistic, "getNMSAchievement", (Class<?>[])new Class[0]).invoke(null, which);
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsAchievement, "name").get(achievement));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public FancyMessage statisticTooltip(Statistic which) {
        Statistic.Type type = which.getType();
        if (type != Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
        }
        try {
            Object statistic = ReflectionUtil.getMethod(this.obcStatistic, "getNMSStatistic", (Class<?>[])new Class[0]).invoke(null, which);
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsStatistic, "name").get(statistic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public FancyMessage statisticTooltip(Statistic which, Material item) {
        Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if ((type == Statistic.Type.BLOCK && item.isBlock()) || type == Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }
        try {
            Object statistic = ReflectionUtil.getMethod(this.obcStatistic, "getMaterialStatistic", (Class<?>[])new Class[0]).invoke(null, which, item);
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsStatistic, "name").get(statistic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public FancyMessage statisticTooltip(Statistic which, EntityType entity) {
        Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if (type != Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }
        try {
            Object statistic = ReflectionUtil.getMethod(this.obcStatistic, "getEntityStatistic", (Class<?>[])new Class[0]).invoke(null, which, entity);
            return this.achievementTooltip((String)ReflectionUtil.getField(this.nmsStatistic, "name").get(statistic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public FancyMessage itemTooltip(String itemJSON) {
        this.onHover("show_item", itemJSON);
        return this;
    }
    
    public FancyMessage itemTooltip(ItemStack itemStack) {
        try {
            Object nmsItem = ReflectionUtil.getMethod(this.obcItemStack, "asNMSCopy", ItemStack.class).invoke(null, itemStack);
            return this.itemTooltip(ReflectionUtil.getMethod(this.nmsItemStack, "save", (Class<?>[])new Class[0]).invoke(nmsItem, this.nmsTagCompound.newInstance()).toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public FancyMessage tooltip(String text) {
        return this.tooltip(text.split("\\n"));
    }
    
    public FancyMessage tooltip(List<String> lines) {
        return this.tooltip((String[])lines.toArray());
    }
    
    public FancyMessage tooltip(String... lines) {
        if (lines.length == 1) {
            this.onHover("show_text", lines[0]);
        }
        else {
            this.itemTooltip(this.makeMultilineTooltip(lines));
        }
        return this;
    }
    
    public FancyMessage then(Object obj) {
        this.messageParts.add(new MessagePart(obj.toString()));
        this.dirty = true;
        return this;
    }
    
    public String toJSONString() {
        if (!this.dirty && this.jsonString != null) {
            return this.jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter((Writer)string);
        try {
            if (this.messageParts.size() == 1) {
                this.latest().writeJson(json);
            }
            else {
                json.beginObject().name("text").value("").name("extra").beginArray();
                for (MessagePart messagePart : this.messageParts) {
                    messagePart.writeJson(json);
                }
                json.endArray().endObject();
                json.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("invalid message");
        }
        this.jsonString = string.toString();
        this.dirty = false;
        return this.jsonString;
    }
    
    public void send(Player player) {
        try {
            Object handle = ReflectionUtil.getHandle(player);
            Object connection = ReflectionUtil.getField(handle.getClass(), "playerConnection").get(handle);
            Object serialized = ReflectionUtil.getMethod(this.nmsChatSerializer, "a", String.class).invoke(null, this.toJSONString());
            Object packet = this.nmsPacketPlayOutChat.getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent")).newInstance(serialized);
            ReflectionUtil.getMethod(connection.getClass(), "sendPacket", (Class<?>[])new Class[0]).invoke(connection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void send(Iterable<Player> players) {
        for (Player player : players) {
            this.send(player);
        }
    }
    
    private MessagePart latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }
    
    private String makeMultilineTooltip(String[] lines) {
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter((Writer)string);
        try {
            json.beginObject().name("id").value(1L);
            json.name("tag").beginObject().name("display").beginObject();
            json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
            json.name("Lore").beginArray();
            for (int i = 1; i < lines.length; ++i) {
                String line = lines[i];
                json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
            }
            json.endArray().endObject().endObject().endObject();
            json.close();
        }
        catch (Exception e) {
            throw new RuntimeException("invalid tooltip");
        }
        return string.toString();
    }
    
    public void onClick(String name, String data) {
        MessagePart latest = this.latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }
    
    private void onHover(String name, String data) {
        MessagePart latest = this.latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }
}
