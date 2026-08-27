package me.inknat.greyhealth.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.inknat.greyhealth.Greyhealth;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GreyHealthRenderMixin {

    @Inject(method = "renderHearts",at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V",
            shift = At.Shift.AFTER,
            ordinal = 0))
    private void renderGreyHeart(GuiGraphics guiGraphics, Player player, int _x, int _y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo ci, @Local(name = "k1") int x, @Local(name = "l1") int y, @Local(name = "l") int heartIndex) {
        int threshold = ((heartIndex+1)*2)-1;
        float currentGreyHealth = player.getData(Greyhealth.GREY_HEALTH);
        int displayGreyHealth = Math.round(currentGreyHealth);

        if (displayGreyHealth < threshold) return;
        boolean halfHeart = displayGreyHealth == threshold;

        RenderSystem.enableBlend();
        var sprite = halfHeart ? Greyhealth.GREY_HEART_HALF : Greyhealth.GREY_HEART_FULL;
        guiGraphics.blitSprite(sprite, x, y, 9, 9);
        RenderSystem.disableBlend();
    }
}
