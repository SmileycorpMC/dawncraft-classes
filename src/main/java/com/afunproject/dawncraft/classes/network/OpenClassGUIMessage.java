package com.afunproject.dawncraft.classes.network;

import com.afunproject.dawncraft.classes.ClassHandler;
import com.afunproject.dawncraft.classes.client.ClientHandler;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.smileycorp.atlas.api.network.AbstractMessage;

import java.util.List;

public class OpenClassGUIMessage extends AbstractMessage {

    private List<DCClass> classes = Lists.newArrayList();

    public OpenClassGUIMessage() {}

    @Override
    public void read(FriendlyByteBuf buf) {
        while (buf.isReadable()) {
            try {
               classes.add(new DCClass(new ResourceLocation(buf.readUtf()), JsonParser.parseString(buf.readUtf()).getAsJsonObject()));
            } catch (Exception e) {}
        }
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        for (DCClass clazz : ClassHandler.getClasses()) {
            if (clazz.isHidden()) continue;
            buf.writeUtf(clazz.getRegistryName().toString());
            buf.writeUtf(clazz.serialize().toString());
        }
    }
    
    @Override
    public void process(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () ->  () -> ClientHandler.displayGUI(getClasses())));
        ctx.setPacketHandled(true);
    }

    public List<DCClass> getClasses() {
        return classes;
    }

    @Override
    public void handle(PacketListener listener) {}

}
