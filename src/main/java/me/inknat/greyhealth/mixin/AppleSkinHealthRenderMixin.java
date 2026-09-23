package me.inknat.greyhealth.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.inknat.greyhealth.Greyhealth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.FoodHelper;

@Mixin(FoodHelper.class)
public class AppleSkinHealthRenderMixin {

    @Definition(id = "getEstimatedHealthIncrement", method = "Lsqueek/appleskin/helpers/FoodHelper;getEstimatedHealthIncrement(IFF)F")
    @Expression("? = getEstimatedHealthIncrement(?, ?, ?)")
    @Inject(
            method = "getEstimatedHealthIncrement(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/food/FoodProperties;)F",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER)
    )
    private static void yummyMixins(Player player, FoodProperties foodProperties, CallbackInfoReturnable<Float> cir,
                                   @Local(name = "healthIncrement") LocalFloatRef localRef) {
        float healthIncrement = localRef.get();
        float currentGreyHealthDifference = player.getData(Greyhealth.GREY_HEALTH)-player.getHealth();

        localRef.set(Math.min(healthIncrement,currentGreyHealthDifference));
    }
}
