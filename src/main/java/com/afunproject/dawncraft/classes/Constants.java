package com.afunproject.dawncraft.classes;

import net.minecraft.util.ResourceLocation;

public class Constants {

    public static final String MODID = "dcclasses";
    public static final String NAME = "DawnCraft Classes";
    public static final String VERSION = "1.1.5";
    public static final String DEPENDENCIES = "required-after:atlaslib@1.1.5";
    
    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
    
    public static String locStr(String path) {
        return loc(path).toString();
    }

}
