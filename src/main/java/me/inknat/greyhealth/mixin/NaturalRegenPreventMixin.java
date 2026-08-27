package me.inknat.greyhealth.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.inknat.greyhealth.Greyhealth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodData.class)
public class NaturalRegenPreventMixin {
    @Definition(id = "flag", local = @Local(type = boolean.class))
    @Expression("flag")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean naturalRegenProxy(boolean original, @Local(argsOnly = true) Player player){
        float health = player.getHealth();
        float grey_health = player.getData(Greyhealth.GREY_HEALTH);
        return grey_health > health;
    }
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    public void healNaturally(Player player, float v, Operation<Void> original) {
        player.setData(Greyhealth.NEXT_HEAL_IS_NATURAL, true);
        float health = player.getHealth();
        float grey_health = player.getData(Greyhealth.GREY_HEALTH);
        v = Math.min(v,grey_health-health);
        original.call(player,v);
    }
}
