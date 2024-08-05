package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.client.AttributeProperties;
import com.afunproject.dawncraft.classes.integration.ParaglidersIntegration;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandEntry {
    
    private final String command;
    private final CommandApplyStage stage;
    
    public CommandEntry(String command, CommandApplyStage stage) {
        this.command = command;
        this.stage = stage;
    }
    
    public void apply(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        ClassesLogger.logInfo("Running command " + command + " for player " + player.getDisplayName().getString());
        String username = player.getGameProfile().getName();
        server.getCommands().performCommand(server.createCommandSourceStack(),
                command.replace("@p", username).replace("@s", username));
    }
    
    public CommandApplyStage getStage() {
        return stage;
    }
    
    @Override
    public String toString() {
        return command + " " + stage;
    }
    
    public static CommandEntry fromJson(JsonElement json, String name) {
        try {
            if (json.isJsonPrimitive()) return new CommandEntry(json.getAsString(), CommandApplyStage.PICK_CLASS);
            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                return new CommandEntry(obj.get("command").getAsString(), obj.has("stage") ? CommandApplyStage.fromJson(obj)
                        : CommandApplyStage.PICK_CLASS);
            }
        } catch (Exception e) {
            ClassesLogger.logError("Failed reading command entry " + json + " for class " + name, e);
        }
        return null;
    }
    
}
