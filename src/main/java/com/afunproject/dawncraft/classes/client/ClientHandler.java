package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.Constants;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID)
public class ClientHandler {

    private static boolean RENDER_SCREEN = false;
    public static final List<DCClass> CLASS_CACHE = Lists.newArrayList();

    @SubscribeEvent
    public static void render(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (!RENDER_SCREEN || event.phase != TickEvent.Phase.START || mc.screen != null) return;
        RENDER_SCREEN = false;
        mc.setScreen(new ClassSelectionScreen(CLASS_CACHE));
        CLASS_CACHE.clear();
    }
    
    @SubscribeEvent
    public static void renderNameplate(RenderNameTagEvent event) {
        if (!(event.getEntity() instanceof RemotePlayer)) return;
        if (Minecraft.getInstance().player.getGameProfile().equals(((RemotePlayer) event.getEntity()).getGameProfile()))
            event.setResult(Event.Result.DENY);
    }

    public static void displayGUI(List<DCClass> cache) {
        CLASS_CACHE.addAll(cache);
        RENDER_SCREEN = true;
    }

}
