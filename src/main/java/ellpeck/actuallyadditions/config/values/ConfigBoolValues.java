package ellpeck.actuallyadditions.config.values;

import ellpeck.actuallyadditions.config.ConfigCategories;
import ellpeck.actuallyadditions.config.ConfigValues;

public enum ConfigBoolValues{

    LEAF_BLOWER_ITEMS("Leaf Blower: Drops Items", ConfigCategories.TOOL_VALUES, true, "If the Leaf Blower lets destroyed Blocks' Drops drop"),
    LEAF_BLOWER_PARTICLES("Leaf Blower: Particles", ConfigCategories.TOOL_VALUES, true, "If the Leaf Blower lets destroyed Blocks have particles when getting destroyed"),
    LEAF_BLOWER_SOUND("Leaf Blower: Sound", ConfigCategories.TOOL_VALUES, true, "If the Leaf Blower makes Sounds"),

    JAM_VILLAGER_EXISTS("Jam Villager: Existence", ConfigCategories.WORLD_GEN, true, "If the Jam Villager and his House exist"),

    GENERATE_QUARTZ("Black Quartz", ConfigCategories.WORLD_GEN, true, "If the Black Quartz generates in the world"),

    EXPERIENCE_DROP("Solidified Experience", ConfigCategories.MOB_DROPS, true, "If the Solidified Experience drops from Mobs"),
    BLOOD_DROP("Blood Fragments", ConfigCategories.MOB_DROPS, false, "If the Blood Fragments drop from Mobs"),
    HEART_DROP("Heart Parts", ConfigCategories.MOB_DROPS, false, "If the Heart Parts drop from Mobs"),
    SUBSTANCE_DROP("Unknown Substance", ConfigCategories.MOB_DROPS, false, "If the Unknown Substance drops from Mobs"),
    PEARL_SHARD_DROP("Ender Pearl Shard", ConfigCategories.MOB_DROPS, true, "If the Ender Pearl Shard drops from Mobs"),
    EMERALD_SHARD_CROP("Emerald Shard", ConfigCategories.MOB_DROPS, true, "If the Emerald Shard drops from Mobs");

    public final String name;
    public final String category;
    public final boolean defaultValue;
    public final String desc;

    ConfigBoolValues(String name, ConfigCategories category, boolean defaultValue, String desc){
        this.name = name;
        this.category = category.name;
        this.defaultValue = defaultValue;
        this.desc = desc;
    }

    public boolean isEnabled(){
        return ConfigValues.boolValues[this.ordinal()];
    }

}