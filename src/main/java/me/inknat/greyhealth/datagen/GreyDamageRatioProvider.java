package me.inknat.greyhealth.datagen;

import me.inknat.greyhealth.Greyhealth;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class GreyDamageRatioProvider extends DataMapProvider {
    public GreyDamageRatioProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(Greyhealth.GREY_DAMAGE_RATIO)
                .add(DamageTypes.FALL, 0.0f, false)
                .add(DamageTypes.DROWN, 0.0f, false)
                .add(DamageTypes.EXPLOSION, 1.0f, false)
                .add(DamageTypes.PLAYER_EXPLOSION, 1.0f, false)
                .add(DamageTypes.LAVA, 1.0f, false);
    }
}
