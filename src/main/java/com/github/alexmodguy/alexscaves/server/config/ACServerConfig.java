package com.github.alexmodguy.alexscaves.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACServerConfig {

    public final ForgeConfigSpec.DoubleValue biomeRarityScale;
    public final ForgeConfigSpec.IntValue nucleeperFuseTime;
    public ACServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("generation");
        biomeRarityScale = builder.comment("the value used to scale the noise function that determines the areas rare cave biomes can spawn in. A higher number means more spread out and larger rare cave biomes.").translation("biome_rarity_scale").defineInRange("biome_rarity_scale", 750.0D, 1D, Double.MAX_VALUE);
        builder.pop();
        builder.push("mob-behavior");
        nucleeperFuseTime = builder.comment("How long (in game ticks) it takes for a nucleeper to explode.").translation("nucleeper_fuse_time").defineInRange("nucleeper_fuse_time", 300, 20, Integer.MAX_VALUE);
        builder.pop();
    }
}