package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultDataGenerator {
    
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_FOLDER = FMLPaths.CONFIGDIR.get().resolve("dcclasses");
    
    public static boolean tryGenerateDataFiles() {
        if (CONFIG_FOLDER.toFile().exists()) return false;
        CONFIG_FOLDER.toFile().mkdirs();
        ModFile mod = FMLLoader.getLoadingModList().getModFileById("hordes").getFile();
        try {
            Files.find(mod.findResource("dcclasses"), Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile())
                    .forEach(DefaultDataGenerator::copyFileFromMod);
            ClassesLogger.logInfo("Generated data files.");
        } catch (Exception e) {
            ClassesLogger.logInfo("Failed to generate data files.");
        }
        return true;
    }
    
    private static void copyFileFromMod(Path path) {
        try {
            FileUtils.copyInputStreamToFile(Files.newInputStream(path),
                    new File(CONFIG_FOLDER.toFile(), path.toString().replace( "config_defaults/", "")));
            ClassesLogger.logInfo("Copied file " + path);
        } catch (Exception e) {
            ClassesLogger.logError("Failed to copy file " + path, e);
        }
    }
    
}
