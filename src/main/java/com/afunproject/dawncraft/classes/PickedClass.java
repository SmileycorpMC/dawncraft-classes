package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.CommandApplyStage;
import com.afunproject.dawncraft.classes.data.CommandContext;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.integration.epicfight.EpicFightIntegration;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.Optional;

public interface PickedClass {

    Optional<DCClass> getDCClass();

    @Nullable
    void setDCClass(DCClass clazz);

    boolean hasPicked();

    boolean hasEffect();

    void applyEffect(ServerPlayer player, boolean addItems);

    void applyStatModifiers(ServerPlayer player);
    
    void setGUIOpen(boolean GUIOpen);
    
    boolean isGUIOpen();
    
    boolean hasStatModifiers();

    CompoundTag save();

    void load(CompoundTag tag);

    class Implementation implements PickedClass {

        private Optional<DCClass> clazz = Optional.empty();
        private boolean hasEffect;
        private boolean GUIOpen;
        private boolean hasStatModifiers;

        @Override
        public Optional<DCClass> getDCClass() {
            return clazz;
        }

        @Override
        public void setDCClass(DCClass clazz) {
            this.clazz = clazz == null ? Optional.empty() : Optional.of(clazz);
        }

        @Override
        public boolean hasPicked() {
            return clazz.isPresent();
        }

        @Override
        public boolean hasEffect() {
            return hasEffect;
        }

        @Override
        public void applyEffect(ServerPlayer player, boolean addItems) {
            if (clazz.isEmpty()) return;
            DCClass clazz = this.clazz.get();
            if (ModList.get().isLoaded("epicfight")) EpicFightIntegration.applySkills(clazz, player);
            applyStatModifiers(player);
            if (addItems) clazz.addItems(player);
            clazz.runCommands(new CommandContext.Builder(player).build(), CommandApplyStage.PICK_CLASS, CommandApplyStage.RESPAWN);
            ClassesLogger.logInfo("Set player " + player.getDisplayName().getString() + " to class " + clazz);
            hasEffect = true;
        }

        @Override
        public void applyStatModifiers(ServerPlayer player) {
            if (clazz.isEmpty()) return;
            clazz.get().applyStatModifiers(player);
            hasEffect = true;
            hasStatModifiers = true;
        }
        
        @Override
        public void setGUIOpen(boolean GUIOpen) {
            this.GUIOpen = GUIOpen;
        }
    
        @Override
        public boolean isGUIOpen() {
            return GUIOpen;
        }
    
        @Override
        public boolean hasStatModifiers() {
            return hasStatModifiers;
        }
    
        @Override
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            if (clazz.isPresent()) tag.putString("class", clazz.get().toString());
            if (hasEffect) tag.putBoolean("hasEffect", hasEffect);
            return tag;
        }

        @Override
        public void load(CompoundTag tag) {
            if (tag.contains("class")) clazz = Optional.of(ClassHandler.getClass(new ResourceLocation(tag.getString("class"))));
            if (tag.contains("hasEffect")) hasEffect = tag.getBoolean("hasEffect");
        }

    }

    class Provider implements ICapabilitySerializable<CompoundTag> {

        private final PickedClass impl;

        public Provider() {
            impl = new Implementation();
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == DCClasses.PICKED_CLASS ? LazyOptional.of(() -> impl).cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return impl.save();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            impl.load(nbt);
        }

    }

}
