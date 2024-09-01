package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.CommandApplyStage;
import com.afunproject.dawncraft.classes.data.DCClass;
import com.afunproject.dawncraft.classes.integration.epicfight.EpicFightIntegration;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.Optional;

public interface PickedClass {

    Optional<DCClass> getDCClass();

    @Nullable
    void setDCClass(DCClass clazz);

    boolean hasPicked();

    boolean hasEffect();

    void applyEffect(EntityPlayerMP player, boolean addItems);

    void applyStatModifiers(EntityPlayerMP player);
    
    void setGUIOpen(boolean GUIOpen);
    
    boolean isGUIOpen();
    
    boolean hasStatModifiers();

    NBTTagCompound save();

    void load(NBTTagCompound tag);

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
        public void applyEffect(EntityPlayerMP player, boolean addItems) {
            if (!clazz.isPresent()) return;
            DCClass clazz = this.clazz.get();
            if (Loader.isModLoaded("epicfight")) EpicFightIntegration.applySkills(clazz, player);
            applyStatModifiers(player);
            if (addItems) clazz.addItems(player);
            clazz.runCommands(player, CommandApplyStage.PICK_CLASS, CommandApplyStage.RESPAWN);
            ClassesLogger.logInfo("Set player " + player.getDisplayName().getFormattedText() + " to class " + clazz);
            hasEffect = true;
        }

        @Override
        public void applyStatModifiers(EntityPlayerMP player) {
            if (!clazz.isPresent()) return;
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
        public NBTTagCompound save() {
            NBTTagCompound tag = new NBTTagCompound();
            if (clazz.isPresent()) tag.setString("class", clazz.get().toString());
            if (hasEffect) tag.setBoolean("hasEffect", hasEffect);
            return tag;
        }

        @Override
        public void load(NBTTagCompound tag) {
            if (tag.hasKey("class")) clazz = Optional.of(ClassHandler.getClass(new ResourceLocation(tag.getString("class"))));
            if (tag.hasKey("hasEffect")) hasEffect = tag.getBoolean("hasEffect");
        }

    }
    
    class Storage implements Capability.IStorage<PickedClass> {
        
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<PickedClass> capability, PickedClass instance, EnumFacing side) {
            return instance.save();
        }
        
        @Override
        public void readNBT(Capability<PickedClass> capability, PickedClass instance, EnumFacing side, NBTBase nbt) {
            instance.load((NBTTagCompound) nbt);
        }
        
    }

    class Provider implements ICapabilitySerializable<NBTTagCompound> {

        private final PickedClass impl;

        public Provider() {
            impl = new Implementation();
        }
        
        @Override
        public boolean hasCapability(Capability<?> cap, EnumFacing side) {
            return cap == DCClasses.PICKED_CLASS;
        }
        
        @Override
        public <T> T getCapability(Capability<T> cap, EnumFacing side) {
            return cap == DCClasses.PICKED_CLASS ? DCClasses.PICKED_CLASS.cast(impl) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return impl.save();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            impl.load(nbt);
        }

    }

}
