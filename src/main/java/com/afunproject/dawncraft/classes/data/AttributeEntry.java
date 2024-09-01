package com.afunproject.dawncraft.classes.data;

import com.afunproject.dawncraft.classes.ClassesLogger;
import com.afunproject.dawncraft.classes.client.AttributeProperties;
import com.afunproject.dawncraft.classes.integration.ParaglidersIntegration;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentBase;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class AttributeEntry {
    
    private final String attribute;
    private final double value;
    private TextComponentBase text;
    
    public AttributeEntry(ResourceLocation attribute, double value) throws Exception {
        this.attribute = attribute
        if (this.attribute == null) throw new NullPointerException("Attribute " + attribute + " not registered.");
        this.value = value;
    }

    public void apply(EntityPlayer player) {
        ClassesLogger.logInfo("Applying attribute " + attribute.getRegistryName() + " with value " + value + " to player " + player.getDisplayName().getString());
        if (ModList.get().isLoaded("paraglider") && ParaglidersIntegration.isStamina(attribute)) ParaglidersIntegration.apply(player, value);
        else player.getAttribute(attribute).setBaseValue(value);
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public double getValue() {
        return value;
    }
    
    public ResourceLocation getName() {
        return attribute.getRegistryName();
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
