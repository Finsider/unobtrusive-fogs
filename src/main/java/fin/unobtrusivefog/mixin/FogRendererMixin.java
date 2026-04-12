package fin.unobtrusivefog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fin.unobtrusivefog.FogRenderType;
import fin.unobtrusivefog.Main;
import fin.unobtrusivefog.Settings;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Unique
    private static final Settings SETTINGS = Main.settings;

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void modifyFog(Camera camera, int viewDistance, DeltaTracker renderTickCounter, float f, ClientLevel clientWorld, CallbackInfoReturnable<Vector4f> cir, @Local FogType cameraSubmersionType, @Local Entity entity, @Local FogData fogData) {

        // peak code writing here
        if (!SETTINGS.applyToAll) {
            if (
                (entity instanceof LivingEntity le && (
                        (!SETTINGS.applyToDarkessFog && le.hasEffect(MobEffects.DARKNESS)) ||
                        (!SETTINGS.applyToBlindnessFog && le.hasEffect(MobEffects.BLINDNESS))
                )) ||

                (!SETTINGS.applyToLavaFog && cameraSubmersionType == FogType.LAVA) ||
                (!SETTINGS.applyToWaterFog && cameraSubmersionType == FogType.WATER) ||
                (!SETTINGS.applyToSnowFog && cameraSubmersionType == FogType.POWDER_SNOW) ||
                (!SETTINGS.applyToAtmosphericFog && cameraSubmersionType == FogType.ATMOSPHERIC) ||

                (!SETTINGS.applyToNetherFog && clientWorld.dimension() == Level.NETHER) ||
                (!SETTINGS.applyToEndFog && clientWorld.dimension() == Level.END)
            ) return;
        }

        float fogEnd = viewDistance * 16;

        if (SETTINGS.renderDistanceFogType == FogRenderType.DISABLED) {
            fogData.renderDistanceEnd = Integer.MAX_VALUE;
        } else if (SETTINGS.renderDistanceFogType == FogRenderType.UNOBTRUSIVE && fogData.renderDistanceEnd < fogEnd) {
            fogData.renderDistanceEnd = fogEnd; // make sure the fog is located at the end of render distance for maximum view.
        }

        if (SETTINGS.environmentalFogType == FogRenderType.DISABLED) {
            fogData.environmentalEnd = Integer.MAX_VALUE;
        } else if (SETTINGS.environmentalFogType == FogRenderType.UNOBTRUSIVE && fogData.environmentalEnd < fogEnd) { // gives off foggy vibe. toggleable.
            fogData.environmentalEnd += fogEnd; // + fogEnd to avoid thick fog,
        }
    }
}
