package com.afunproject.dawncraft.classes.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CommandContext {
    
    private final ServerPlayer player;
    private final MinecraftServer server;
    private final Entity entity;
    
    private CommandContext(Builder builder) {
        this.player = builder.player;
        this.server = builder.server;
        this.entity = builder.entity;
    }
    
    public ServerPlayer getPlayer() {
        return player;
    }
    
    public MinecraftServer getServer() {
        return server;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public static class Builder {
        
        private final ServerPlayer player;
        private final MinecraftServer server;
        private Entity entity;
        
        public Builder(ServerPlayer player) {
            this.player = player;
            this.entity = player;
            this.server = player.getServer();
        }
        
        public Builder entity(Entity entity) {
            this.entity = entity;
            return this;
        }
        
        public CommandContext build () {
            return new CommandContext(this);
        }
        
    }
    
}
