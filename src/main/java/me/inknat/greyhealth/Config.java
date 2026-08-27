package me.inknat.greyhealth;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final Config CONFIG;
    public static final ModConfigSpec SPEC;
    static {
        Pair<Config, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Config::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }
    public ModConfigSpec.DoubleValue defaultDamageRatio;

    private Config(ModConfigSpec.Builder builder) {
        builder.comment("Must be between 0.0 and 1.0. At 0.0, grey health will not be affected. At 1.0, 100% of the damage taken will reduce grey health.");
        defaultDamageRatio = builder.defineInRange("default_damage_ratio",0.5,0.0,1.0);
    }
}
