package com.afunproject.dawncraft.classes.integration.epicfight.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.gameasset.Armatures;

public class AnimationPlayerPatch extends AbstractClientPlayerPatch<AbstractClientPlayer> {
    
    public AnimationPlayerPatch(RemotePlayer player) {
        original = player;
        armature = Armatures.getArmatureFor(this);
        animator = new Animator(this);
        animator.init();
    }
    
    public void update() {
        animator.tick();
        getCurrentAnimation().tick(this);
    }
    
    public DynamicAnimation getCurrentAnimation() {
        return ((Animator)animator).getCurrentAnimation();
    }
    
    public static class Animator extends ClientAnimator {
        public Animator(AnimationPlayerPatch playerPatch) {
            super(playerPatch);
        }
        
        @Override
        public void tick() {
            playAnimation(getLivingMotion(entitypatch.currentLivingMotion), 0f);
        }
        
        public DynamicAnimation getCurrentAnimation() {
            return baseLayer.animationPlayer.getAnimation();
        }
        
    }
}
