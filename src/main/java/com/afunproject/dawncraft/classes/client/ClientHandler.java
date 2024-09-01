package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.Constants;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Constants.MODID)
public class ClientHandler {

    private static boolean RENDER_SCREEN = false;
    public static final List<DCClass> CLASS_CACHE = Lists.newArrayList();

    @SubscribeEvent
    public static void render(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!RENDER_SCREEN || event.phase != TickEvent.Phase.START || mc.currentScreen != null) return;
        RENDER_SCREEN = false;
        mc.displayGuiScreen(new ClassSelectionScreen(CLASS_CACHE));
        CLASS_CACHE.clear();
    }
    
    @SubscribeEvent
    public static void renderNameplate(RenderNameplateEvent event) {
        if (!(event.getEntity() instanceof RemotePlayer)) return;
        if (Minecraft.getInstance().player.getGameProfile().equals(((RemotePlayer) event.getEntity()).getGameProfile()))
            event.setResult(Event.Result.DENY);
    }

    public static void displayGUI(List<DCClass> cache) {
        CLASS_CACHE.addAll(cache);
        RENDER_SCREEN = true;
    }

}
