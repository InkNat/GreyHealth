package me.inknat.greyhealth;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.slf4j.Logger;
import java.util.function.Supplier;

@Mod(Greyhealth.MODID)
public class Greyhealth {
    public static final String MODID = "greyhealth";
    public static final String GREY_HEALTH_ID = "grey_health";
    public static final String NEXT_HEAL_IS_NATURAL_ID = "next_heal_is_natural";
    public static final ResourceLocation GREY_HEART_FULL = ResourceLocation.fromNamespaceAndPath(MODID,"grey_heart_full");
    public static final ResourceLocation GREY_HEART_HALF = ResourceLocation.fromNamespaceAndPath(MODID,"grey_heart_half");

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final DataMapType<DamageType, Float> GREY_DAMAGE_RATIO = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(MODID, "grey_damage_ratio"), Registries.DAMAGE_TYPE, Codec.FLOAT
    ).synced(Codec.FLOAT, true).build();

    public static final Supplier<AttachmentType<Float>> GREY_HEALTH = ATTACHMENT_TYPES.register(GREY_HEALTH_ID,
            () -> AttachmentType.builder(() -> 20f)
                    .serialize(Codec.FLOAT.fieldOf(GREY_HEALTH_ID).codec())
                    .sync(ByteBufCodecs.FLOAT)
                    .build());
    public static final Supplier<AttachmentType<Boolean>> NEXT_HEAL_IS_NATURAL = ATTACHMENT_TYPES.register(NEXT_HEAL_IS_NATURAL_ID,
            () -> AttachmentType.builder(() -> false)
                    .build());

    public static final Logger LOGGER = LogUtils.getLogger();

    public Greyhealth(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(new ModBusEvents());
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    @SubscribeEvent
    private void onEntityHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        boolean isNatural = player.getData(NEXT_HEAL_IS_NATURAL);
        if (isNatural) {
            player.setData(NEXT_HEAL_IS_NATURAL,false);
            return;
        }
        float max_health = (float) player.getAttribute(Attributes.MAX_HEALTH).getValue();
        if (!player.hasData(GREY_HEALTH)) {
            player.setData(GREY_HEALTH, max_health);
        }
        float previous = player.getData(GREY_HEALTH);
        previous += (event.getAmount());
        if (previous > max_health) previous = max_health;

        player.setData(GREY_HEALTH, previous);
    }

    @SubscribeEvent
    private void onEntityDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!player.hasData(GREY_HEALTH)) {
            float max_health = (float) player.getAttribute(Attributes.MAX_HEALTH).getValue();
            player.setData(GREY_HEALTH, max_health);
        }
        float previous = player.getData(GREY_HEALTH);
        float grey_damage = (event.getNewDamage());

        var damageTypeRegistry = event.getEntity().level().registryAccess().registry(Registries.DAMAGE_TYPE).get();
        var damageTypeHolder = damageTypeRegistry.wrapAsHolder(event.getSource().type());

        Float damageTypeData = damageTypeHolder.getData(GREY_DAMAGE_RATIO);
        float multiplier = damageTypeData == null ? (float) Config.CONFIG.defaultDamageRatio.getAsDouble() : damageTypeData;

        previous -= grey_damage * multiplier;
        if (previous < 0) previous = 0;
        player.setData(GREY_HEALTH, previous);
        LOGGER.debug("{}",player.getData(GREY_HEALTH));
    }
}
