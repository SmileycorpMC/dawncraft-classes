package com.afunproject.dawncraft.classes.integration.epicfight.client;

import com.afunproject.dawncraft.classes.client.ClassSlot;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.Skill;

import java.util.List;

public class SkillSlot extends ClassSlot {
    
    private static final int TEXT_WIDTH = 20;
    
    private final Skill skill;
    private final List<Component> tooltip = Lists.newArrayList();
    
    public SkillSlot(String name, int x, int y) {
        super(x, y, 16, 16);
        skill = SkillManager.getSkill(name);
        tooltip.add(Component.translatable(skill.getTranslationKey()).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("skill.epicfight." + skill.getCategory().toString().toLowerCase() + ".category").withStyle(ChatFormatting.BLUE));
        int position = 0;
        String str = Component.translatable(skill.getTranslationKey() + ".tooltip", skill.getTooltipArgsOfScreen(Lists.newArrayList()).toArray(new Object[0])).getString();
        while (position < str.length()) {
            int size = Math.min(TEXT_WIDTH, str.length() - position);
            int newPos = position + size;
            if (str.substring(position, newPos).contains("\n")) {
                int i = str.substring(position, newPos).indexOf("\n");
                tooltip.add(Component.literal(str.substring(position, position + i)));
                position = position + i + 1;
                continue;
            }
            if (size < TEXT_WIDTH || newPos >= str.length()) {
                tooltip.add(Component.literal(str.substring(position)));
                break;
            }
            for (int i = 0; i <= size; i++) {
                if (i == size) {
                    tooltip.add(Component.literal(str.substring(position, newPos + 1)));
                    position = newPos;
                    break;
                } else if (str.charAt(newPos - i) == ' ') {
                    tooltip.add(Component.literal(str.substring(position, newPos - i + 1)));
                    position = newPos - i + 1;
                    break;
                }
            }
        }
    }
    
    @Override
    public List<Component> getTooltip() {
        return tooltip;
    }
    
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        gui.blit(skill.getSkillTexture(), x, y, 1, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
    }
    
}
