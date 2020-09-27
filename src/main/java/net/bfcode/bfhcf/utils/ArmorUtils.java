package net.bfcode.bfhcf.utils;

import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import java.util.stream.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;

import org.bukkit.potion.*;

import net.bfcode.bfhcf.HCFaction;
import net.minecraft.util.gnu.trove.Version;

import org.bukkit.inventory.meta.*;
import org.bukkit.block.*;
import org.bukkit.*;

public class ArmorUtils implements Cloneable
{
    private static HCFaction ce;
    private static Version version;
    private static boolean useNewMaterial;
    private Material material;
    private int damage;
    private String name;
    private List<String> lore;
    private int amount;
    private String player;
    private boolean isHash;
    private boolean isURL;
    private boolean isHead;
    private HashMap<Enchantment, Integer> enchantments;
    private boolean unbreakable;
    private boolean hideItemFlags;
    private boolean glowing;
    private ItemStack referenceItem;
    private boolean isMobEgg;
    private EntityType entityType;
    private boolean isTippedArrow;
    private PotionType potionType;
    private Color potionColor;
    private boolean isPotion;
    private Color armorColor;
    private boolean isLeatherArmor;
    private List<Pattern> patterns;
    private boolean isBanner;
    private boolean isShield;
    private int customModelData;
    private boolean useCustomModelData;
    private HashMap<String, String> namePlaceholders;
    private HashMap<String, String> lorePlaceholders;
    
    public ArmorUtils() {
        this.material = Material.STONE;
        this.damage = 0;
        this.name = "";
        this.lore = new ArrayList<String>();
        this.amount = 1;
        this.player = "";
        this.isHash = false;
        this.isURL = false;
        this.isHead = false;
        this.enchantments = new HashMap<Enchantment, Integer>();
        this.unbreakable = false;
        this.hideItemFlags = false;
        this.glowing = false;
        this.entityType = EntityType.BAT;
        this.isTippedArrow = false;
        this.potionType = null;
        this.potionColor = null;
        this.isPotion = false;
        this.armorColor = null;
        this.isLeatherArmor = false;
        this.patterns = new ArrayList<Pattern>();
        this.isBanner = false;
        this.isShield = false;
        this.customModelData = 0;
        this.useCustomModelData = false;
        this.isMobEgg = false;
        this.namePlaceholders = new HashMap<String, String>();
        this.lorePlaceholders = new HashMap<String, String>();
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public ArmorUtils setMaterial(final Material material) {
        this.material = material;
        this.isHead = (material == (ArmorUtils.useNewMaterial ? Material.matchMaterial("PLAYER_HEAD") : Material.matchMaterial("SKULL_ITEM")));
        return this;
    }
    
    public ArmorUtils setMaterial(String materialString) {
        String metaDataString = "";
        if (materialString.contains(":")) {
            final String[] split = materialString.split(":");
            materialString = split[0];
            metaDataString = split[1];
            if (metaDataString.contains("#")) {
                final String modelData = metaDataString.split("#")[1];
                if (Methods.isInt(modelData)) {
                    this.useCustomModelData = true;
                    this.customModelData = Integer.parseInt(modelData);
                }
            }
            metaDataString = metaDataString.replace("#" + this.customModelData, "");
            if (Methods.isInt(metaDataString)) {
                this.damage = Integer.parseInt(metaDataString);
            }
            else {
                for (final String option : split) {
                    if (this.potionType == null) {
                        this.potionType = this.getPotionType(PotionEffectType.getByName(option));
                    }
                    if (this.potionColor == null) {
                        this.potionColor = getColor(option);
                    }
                    if (this.armorColor == null) {
                        this.armorColor = getColor(option);
                    }
                }
            }
        }
        else if (materialString.contains("#")) {
            final String[] b = materialString.split("#");
            materialString = b[0];
            if (Methods.isInt(b[1])) {
                this.useCustomModelData = true;
                this.customModelData = Integer.parseInt(b[1]);
            }
        }
        final Material material = Material.matchMaterial(materialString);
        if (material != null) {
            this.material = material;
        }
        final String name = this.material.name();
        switch (name) {
            case "PLAYER_HEAD":
            case "SKULL_ITEM": {
                this.isHead = true;
                break;
            }
            case "TIPPED_ARROW": {
                this.isTippedArrow = true;
                break;
            }
            case "POTION":
            case "SPLASH_POTION": {
                this.isPotion = true;
                break;
            }
            case "LEATHER_HELMET":
            case "LEATHER_CHESTPLATE":
            case "LEATHER_LEGGINGS":
            case "LEATHER_BOOTS": {
                this.isLeatherArmor = true;
                break;
            }
            case "BANNER": {
                this.isBanner = true;
                break;
            }
            case "SHIELD": {
                this.isShield = true;
                break;
            }
        }
        if (this.material.name().contains("BANNER")) {
            this.isBanner = true;
        }
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArmorUtils setName(String name) {
        if (name != null) {
            this.name = CC.translate(name);
        }
        return this;
    }
    
//    public ArmorUtils setMaterial(final String newMaterial, final String oldMaterial) {
//        return this.setMaterial(ArmorUtils.ce.useNewMaterial() ? newMaterial : oldMaterial);
//    }
    
    private static Color getColor(final String color) {
        if (color != null) {
            final String upperCase = color.toUpperCase();
            switch (upperCase) {
                case "AQUA": {
                    return Color.AQUA;
                }
                case "BLACK": {
                    return Color.BLACK;
                }
                case "BLUE": {
                    return Color.BLUE;
                }
                case "FUCHSIA": {
                    return Color.FUCHSIA;
                }
                case "GRAY": {
                    return Color.GRAY;
                }
                case "GREEN": {
                    return Color.GREEN;
                }
                case "LIME": {
                    return Color.LIME;
                }
                case "MAROON": {
                    return Color.MAROON;
                }
                case "NAVY": {
                    return Color.NAVY;
                }
                case "OLIVE": {
                    return Color.OLIVE;
                }
                case "ORANGE": {
                    return Color.ORANGE;
                }
                case "PURPLE": {
                    return Color.PURPLE;
                }
                case "RED": {
                    return Color.RED;
                }
                case "SILVER": {
                    return Color.SILVER;
                }
                case "TEAL": {
                    return Color.TEAL;
                }
                case "WHITE": {
                    return Color.WHITE;
                }
                case "YELLOW": {
                    return Color.YELLOW;
                }
                default: {
                    try {
                        final String[] rgb = color.split(",");
                        return Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                    }
                    catch (Exception ex) {}
                    break;
                }
            }
        }
        return null;
    }
    
    public PotionType getPotionType() {
        return this.potionType;
    }
    
    public void setPotionType(final PotionType potionType) {
        this.potionType = potionType;
    }
    
    private PotionType getPotionType(final PotionEffectType type) {
        if (type != null) {
            if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
                return PotionType.FIRE_RESISTANCE;
            }
            if (type.equals(PotionEffectType.HARM)) {
                return PotionType.INSTANT_DAMAGE;
            }
            if (type.equals(PotionEffectType.HEAL)) {
                return PotionType.INSTANT_HEAL;
            }
            if (type.equals(PotionEffectType.INVISIBILITY)) {
                return PotionType.INVISIBILITY;
            }
            if (type.equals(PotionEffectType.getByName("LUCK"))) {
                return PotionType.valueOf("LUCK");
            }
            if (type.equals(PotionEffectType.NIGHT_VISION)) {
                return PotionType.NIGHT_VISION;
            }
            if (type.equals(PotionEffectType.POISON)) {
                return PotionType.POISON;
            }
            if (type.equals(PotionEffectType.REGENERATION)) {
                return PotionType.REGEN;
            }
            if (type.equals(PotionEffectType.SLOW)) {
                return PotionType.SLOWNESS;
            }
            if (type.equals(PotionEffectType.SPEED)) {
                return PotionType.SPEED;
            }
            if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
                return PotionType.STRENGTH;
            }
            if (type.equals(PotionEffectType.WATER_BREATHING)) {
                return PotionType.WATER_BREATHING;
            }
            if (type.equals(PotionEffectType.WEAKNESS)) {
                return PotionType.WEAKNESS;
            }
        }
        return null;
    }
    
    static {
        ArmorUtils.ce = HCFaction.getPlugin();
    }
}
