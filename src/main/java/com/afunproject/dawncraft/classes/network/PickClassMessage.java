package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.ClassHandler;
import com.afunproject.dawncraft.classes.DCClasses;
import com.afunproject.dawncraft.classes.PickedClass;
import com.afunproject.dawncraft.classes.event.PickClassEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import net.smileycorp.atlas.api.network.AbstractMessage;

public class PickClassMessage extends AbstractMessage {

    private ResourceLocation loc;

    public PickClassMessage() {}

    public PickClassMessage(ResourceLocation loc) {
        this.loc = loc;
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        loc = buf.readResourceLocation();
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(loc);
    }

    @Override
    public void handle(PacketListener listener) {}
    
    @Override
    public void process(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> apply(ctx.getSender()));
        ctx.setPacketHandled(true);
    }

    public void apply(ServerPlayer sender) {
        LazyOptional<PickedClass> optional = sender.getCapability(DCClasses.PICKED_CLASS);
        if (optional.isPresent()) {
            PickedClass cap = optional.orElseGet(null);
            PickClassEvent event = new PickClassEvent.Pre(sender, ClassHandler.getClass(loc), true);
            MinecraftForge.EVENT_BUS.post(event);
            cap.setDCClass(event.getDCClass());
            cap.applyEffect(sender, true);
            cap.setGUIOpen(false);
            MinecraftForge.EVENT_BUS.post(new PickClassEvent.Post(sender, event.getDCClass(), true));
        }
    }

}
