package com.afunproject.dawncraft.classes.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.function.Function;

public class CommandApplyStage {
    
    private static final Map<String, CommandApplyStage> STATIC_STAGES = Maps.newHashMap();
    private static final Map<String, Function<JsonObject, CommandApplyStage>> DYNAMIC_STAGES = Maps.newHashMap();
    
    public static final CommandApplyStage PICK_CLASS = register("pick_class");
    public static final CommandApplyStage RESPAWN = register("respawn");
    public static final CommandApplyStage ATTACK = register("attack");
    public static final CommandApplyStage HURT = register("hurt");
    public static final CommandApplyStage DIE = register("die");
    public static final CommandApplyStage KILL = register("kill");
    
    private CommandApplyStage() {}
    
    static {
        registerDynamic("tick", Ticking::fromJson);
    }
    
    public static CommandApplyStage register(String name) {
        return register(name, new CommandApplyStage());
    }
    
    private static CommandApplyStage register(String name, CommandApplyStage stage) {
        STATIC_STAGES.put(name, stage);
        return stage;
    }
    
    private static void registerDynamic(String name, Function<JsonObject, CommandApplyStage> func) {
        DYNAMIC_STAGES.put(name, func);
    }
    
    public static CommandApplyStage fromJson(JsonObject obj) {
        if (obj.has("stage")) {
            String stage = obj.get("stage").getAsString();
            if (DYNAMIC_STAGES.containsKey(stage)) return DYNAMIC_STAGES.get(stage).apply(obj);
            if (STATIC_STAGES.containsKey(stage)) return STATIC_STAGES.get(stage);
        }
        return CommandApplyStage.PICK_CLASS;
    }
    
    public static class Ticking extends CommandApplyStage {
        
        private final int interval;
        
        private Ticking(int interval) {
            this.interval = interval;
        }
        
        public int getInterval() {
            return interval;
        }
        
        public static CommandApplyStage fromJson(JsonObject obj) {
            return new Ticking(obj.has("interval") ? obj.get("interval").getAsInt() : 0);
        }
        
    }
    
}
