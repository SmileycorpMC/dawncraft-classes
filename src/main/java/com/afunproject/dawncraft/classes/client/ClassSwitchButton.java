package com.afunproject.dawncraft.classes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ClassSwitchButton extends AbstractButton {
    
    private final ClassSelectionScreen screen;
    private final boolean back;
    
    public ClassSwitchButton(ClassSelectionScreen screen, int x, int y, boolean back) {
        super(x, y, 11, 17, Component.nullToEmpty(null));
        this.screen = screen;
        this.back = back;
    }
    
    @Override
    public void onPress() {
        screen.switchPage(back ? -1 : 1);
    }
    
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ClassSelectionScreen.TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        gui.blit(ClassSelectionScreen.TEXTURE, getX(),getY(), back ? 15 : 1, isHovered ? 93 : 75, width, height);
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    
}
