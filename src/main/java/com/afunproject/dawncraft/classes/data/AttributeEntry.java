package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.client.AttributeProperties;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeEntry {

    private final Attribute attribute;
    private final double value;
    private final ResourceLocation registry;
    private MutableComponent text;
    
    public AttributeEntry(ResourceLocation attribute, double value) throws Exception {
        this.attribute = ForgeRegistries.ATTRIBUTES.getValue(attribute);
        this.registry = attribute;
        if (this.attribute == null) throw new NullPointerException("Attribute " + attribute + " not registered.");
        this.value = value;
    }

    public void apply(Player player) {
        ClassesLogger.logInfo("Applying attribute " + registry + " with value " + value + " to player " + player.getDisplayName().getString());
        player.getAttribute(attribute).setBaseValue(value);
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public double getValue() {
        return value;
    }
    
    public ResourceLocation getName() {
        return registry;
    }
    
    public MutableComponent getText() {
        if (text == null) text = AttributeProperties.INSTANCE.getText(attribute, value);
        return text;
    }
    
    public TextColor getTextColour() {
        return AttributeProperties.INSTANCE.getTextColour(attribute);
    }
    
    @Override
    public String toString() {
        return getName() + " " + value;
    }

}
