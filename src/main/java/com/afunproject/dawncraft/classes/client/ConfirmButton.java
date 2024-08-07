package com.afunproject.dawncraft.classes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ConfirmButton extends Button {
    
    public ConfirmButton(ClassSelectionScreen screen, int x, int y) {
        super(x, y, 60, 20, Component.translatable("button.dcclasses.confirm"), b -> screen.confirm(), DEFAULT_NARRATION);
    }
    
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ClassSelectionScreen.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        gui.blit(ClassSelectionScreen.TEXTURE, getX(), getY(), 0, isHovered ? 151 : 131, width, height);
        gui.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, 0xFFFFFF);
    }
    
}
