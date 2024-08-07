package com.afunproject.dawncraft.classes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class ConfirmButtom extends Button {
    
    public ConfirmButtom(ClassSelectionScreen screen, int x, int y) {
        super(x, y, 60, 20, new TranslatableComponent("button.dcclasses.confirm"), b -> screen.confirm());
    }
    
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ClassSelectionScreen.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int i = getYImage(isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(poseStack, x, y, 0, 111 + i * 20, width, height);
        drawCenteredString(poseStack, font, getMessage(), x + width / 2, y + (height - 8) / 2, 0xFFFFFF);
    }
    
}
