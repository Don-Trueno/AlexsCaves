package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

    protected ClientLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    //use the "time of day" to get daytime independent sky of cave biomes.
    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true)
    private void ac_getSkyColor_timeOfDay(Vec3 position, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides.get()) {
            float override = ACBiomeRegistry.calculateBiomeSkyOverride(Minecraft.getInstance().cameraEntity);
            if (override > 0.0F) {
                Vec3 samplePos = Minecraft.getInstance().cameraEntity.position().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
                BiomeManager biomemanager = this.getBiomeManager();
                Vec3 prevVec3 = cir.getReturnValue();
                Vec3 sampledVec3 = CubicSampler.gaussianSampleVec3(samplePos, (x, y, z) -> {
                    return Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).value().getSkyColor());
                });
                cir.setReturnValue(prevVec3.add(sampledVec3.subtract(prevVec3).scale(override)));
            }
        }
    }

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyDarken(F)F",
            at = @At(
                    value = "RETURN"
            ),
            cancellable = true)
    private void ac_getSkyDarken_timeOfDay(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides.get()) {
            float skyDarken = cir.getReturnValue();
            float override = ACBiomeRegistry.calculateBiomeSkyOverride(Minecraft.getInstance().cameraEntity);
            if(override > 0.0F){
                cir.setReturnValue(Math.max(skyDarken, override));
            }
        }
    }
}