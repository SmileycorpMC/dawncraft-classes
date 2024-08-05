package com.afunproject.dawncraft.classes.event;

import com.afunproject.dawncraft.classes.data.DCClass;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PickClassEvent extends PlayerEvent {
    
    protected DCClass dcClass;
    private final boolean isCommand;
    
    public PickClassEvent(Player player, DCClass dcClass, boolean isCommand) {
        super(player);
        this.dcClass = dcClass;
        this.isCommand = isCommand;
    }
    
    public DCClass getDCClass() {
        return dcClass;
    }
    
    public boolean isCommand() {
        return isCommand;
    }
    
    public static class Pre extends PickClassEvent {
        
        public Pre(Player player, DCClass dcClass, boolean isCommand) {
            super(player, dcClass, isCommand);
        }
        
        public void setDCClass(DCClass dcClass) {
            this.dcClass = dcClass;
        }
        
    }
    
    public static class Post extends PickClassEvent {
        
        public Post(Player player, DCClass dcClass, boolean isCommand) {
            super(player, dcClass, isCommand);
        }
        
    }
    
}
