package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.*;
import com.afunproject.dawncraft.classes.network.NetworkHandler;
import com.afunproject.dawncraft.classes.network.OpenClassGUIMessage;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

public class EventHandler {
    
    @SubscribeEvent
    public void addResourceReload(AddReloadListenerEvent event) {
        event.addListener(DCClassLoader.INSTANCE);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PickedClass.class);
    }

    @SubscribeEvent
    public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof Player &! (entity instanceof FakePlayer)) {
            event.addCapability(Constants.loc("picked_class"), new PickedClass.Provider());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer)) return;
        LazyOptional<PickedClass> optional = player.getCapability(DCClasses.PICKED_CLASS);
        if (!optional.isPresent()) return;
        PickedClass cap = optional.orElseGet(null);
        if (!cap.hasPicked() &! ClassHandler.getClasses().isEmpty()) {
            NetworkHandler.NETWORK_INSTANCE.sendTo(new OpenClassGUIMessage(),
                    ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            cap.setGUIOpen(true);
        }
    }

    @SubscribeEvent
    public void tick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() == null) return;
        Entity player = event.getEntity();
        if (!(player instanceof ServerPlayer)) return;
        LazyOptional<PickedClass> optional = player.getCapability(DCClasses.PICKED_CLASS);
        if (!optional.isPresent()) return;
        PickedClass cap = optional.orElseGet(null);
        if (!cap.hasPicked() &! ClassHandler.getClasses().isEmpty() &! cap.isGUIOpen()) {
            NetworkHandler.NETWORK_INSTANCE.sendTo(new OpenClassGUIMessage(),
                    ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            cap.setGUIOpen(true);
        }
        if (cap.hasPicked() &! cap.hasStatModifiers()) cap.applyStatModifiers((ServerPlayer) player);
        Optional<DCClass> clazz = cap.getDCClass();
        if (clazz.isEmpty()) return;
        for (CommandEntry entry : clazz.get().getCommands()) {
            CommandApplyStage stage = entry.getStage();
            if (stage instanceof CommandApplyStage.Ticking && player.tickCount %
                    ((CommandApplyStage.Ticking) stage).getInterval() == 0)
                entry.apply(new CommandContext.Builder((ServerPlayer) player).build());
        }
    }
    
    @SubscribeEvent
    public void damage(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        Entity entity = event.getEntity();
        LazyOptional<PickedClass> optional = entity.getCapability(DCClasses.PICKED_CLASS);
        if (!optional.isPresent()) return;
        PickedClass cap = optional.orElseGet(null);
        if (cap.isGUIOpen()) event.setCanceled(true);
        Optional<DCClass> clazz = cap.getDCClass();
        if (clazz.isEmpty()) return;
        clazz.get().runCommands(new CommandContext.Builder((ServerPlayer) entity).entity(event.getSource().getEntity()).build(), CommandApplyStage.HURT);
    }
    
    @SubscribeEvent
    public void livingHurtEvent(LivingHurtEvent event) {
        Entity attacker = event.getSource().getDirectEntity();
        if (!(attacker instanceof ServerPlayer)) return;
        LazyOptional<PickedClass> optional = attacker.getCapability(DCClasses.PICKED_CLASS);
        if (!optional.isPresent()) return;
        Optional<DCClass> clazz = optional.orElseGet(null).getDCClass();
        if (clazz.isEmpty()) return;
        clazz.get().runCommands(new CommandContext.Builder((ServerPlayer) attacker).entity(event.getEntity()).build(), CommandApplyStage.ATTACK);
    }
    
    @SubscribeEvent
    public void dieEvent(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer)) return;
        LazyOptional<PickedClass> optional = entity.getCapability(DCClasses.PICKED_CLASS);
        if (!optional.isPresent()) return;
        PickedClass cap = optional.orElseGet(null);
        if (cap.isGUIOpen()) event.setCanceled(true);
        Optional<DCClass> clazz = cap.getDCClass();
        if (clazz.isEmpty()) return;
        clazz.get().runCommands(new CommandContext.Builder((ServerPlayer) entity).entity(event.getSource().getEntity()).build(), CommandApplyStage.DIE);
    }
    
    @SubscribeEvent
    public void killEvent(LivingDeathEvent event) {
        Entity entity = event.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) return;
        LazyOptional<PickedClass> optional = entity.getCapability(DCClasses.PICKED_CLASS);
        if (!optional.isPresent()) return;
        PickedClass cap = optional.orElseGet(null);
        if (cap.isGUIOpen()) event.setCanceled(true);
        Optional<DCClass> clazz = cap.getDCClass();
        if (clazz.isEmpty()) return;
        clazz.get().runCommands(new CommandContext.Builder((ServerPlayer) entity).entity(event.getEntity()).build(), CommandApplyStage.KILL);
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (!(event.getPlayer() instanceof ServerPlayer)) return;
        Player original = event.getOriginal();
        Player player = event.getPlayer();
        original.reviveCaps();
        if (!original.getGameProfile().equals(player.getGameProfile())) return;
        LazyOptional<PickedClass> optionalOld = original.getCapability(DCClasses.PICKED_CLASS);
        LazyOptional<PickedClass> optional = player.getCapability(DCClasses.PICKED_CLASS);
        if (optionalOld.isPresent() && optional.isPresent()) {
            PickedClass cap = optional.orElseGet(null);
            cap.load(optionalOld.orElseGet(null).save());
            Optional<DCClass> clazz = cap.getDCClass();
            if (clazz.isEmpty()) return;
            clazz.get().runCommands(new CommandContext.Builder((ServerPlayer) player).build(), CommandApplyStage.RESPAWN);
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            PickClassCommand.register(dispatcher);
    }

}
