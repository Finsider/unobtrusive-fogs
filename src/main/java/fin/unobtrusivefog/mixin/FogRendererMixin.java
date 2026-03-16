package fin.unobtrusivefog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fin.unobtrusivefog.FogRenderType;
import fin.unobtrusivefog.Main;
import fin.unobtrusivefog.Settings;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
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

    @Inject(
            method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"
            )
    )
    private void modifyFog(Camera camera, int viewDistance, boolean thick, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world, CallbackInfoReturnable<Vector4f> cir, @Local CameraSubmersionType cameraSubmersionType, @Local Entity entity, @Local FogData fogData) {

        // peak code writing here
        if (!SETTINGS.applyToAll) {
            if (
                (entity instanceof LivingEntity le && (
                        (!SETTINGS.applyToDarkessFog && le.hasStatusEffect(StatusEffects.DARKNESS)) ||
                        (!SETTINGS.applyToBlindnessFog && le.hasStatusEffect(StatusEffects.BLINDNESS))
                )) ||

                (!SETTINGS.applyToLavaFog && cameraSubmersionType == CameraSubmersionType.LAVA) ||
                (!SETTINGS.applyToWaterFog && cameraSubmersionType == CameraSubmersionType.WATER) ||
                (!SETTINGS.applyToSnowFog && cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) ||
                (!SETTINGS.applyToAtmosphericFog && cameraSubmersionType == CameraSubmersionType.ATMOSPHERIC) ||

                (!SETTINGS.applyToNetherFog && world.getRegistryKey() == World.NETHER) ||
                (!SETTINGS.applyToEndFog && world.getRegistryKey() == World.END)
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
