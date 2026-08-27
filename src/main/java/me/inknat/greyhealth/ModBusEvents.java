package me.inknat.greyhealth;

import me.inknat.greyhealth.datagen.GreyDamageRatioProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.concurrent.CompletableFuture;

public class ModBusEvents {
    @SubscribeEvent // on the mod event bus
    public void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(Greyhealth.GREY_DAMAGE_RATIO);
    }
    @SubscribeEvent
    public void commonSetup(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // other providers here
        generator.addProvider(
                event.includeServer(),
                new GreyDamageRatioProvider(output, lookupProvider)
        );
    }
}
