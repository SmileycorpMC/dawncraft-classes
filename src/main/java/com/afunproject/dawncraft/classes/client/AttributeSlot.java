package com.afunproject.dawncraft.classes.client;

import com.afunproject.dawncraft.classes.data.AttributeEntry;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AttributeSlot extends ClassSlot {
    
    private final AttributeEntry attribute;
    
    public AttributeSlot(AttributeEntry attribute, int width, int x, int y) {
        super(x, y, width,  11);
        this.attribute = attribute;
    }
    
    @Override
    public List<Component> getTooltip() {
        return Lists.newArrayList(Component.translatable(attribute.getAttribute().getDescriptionId()).append(Component.literal(": " + attribute.getValue())));
    }
    
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = new PoseStack();
        ResourceLocation loc = attribute.getName();
        RenderSystem.enableBlend();
        gui.blit( new ResourceLocation(loc.getNamespace(), "textures/attribute/" + loc.getPath() + ".png"), x + 1, y + 1, 1, 0, 0, 9, 9, 9, 9);
        gui.drawString(minecraft.font, attribute.getText(), x + 11, y + 2, attribute.getTextColour().getValue());
    }
    
}
