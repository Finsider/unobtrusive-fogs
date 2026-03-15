package fin.unobtrusivefogs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fin.unobtrusivefogs.FogRenderType;
import fin.unobtrusivefogs.Main;
import fin.unobtrusivefogs.Settings;
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

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"))
    private void modifyFog(Camera camera, int viewDistance, RenderTickCounter renderTickCounter, float f, ClientWorld clientWorld, CallbackInfoReturnable<Vector4f> cir, @Local CameraSubmersionType cameraSubmersionType, @Local Entity entity, @Local FogData fogData) {

        // peak code writing here
        if (!SETTINGS.applyToAll) {
            if (entity instanceof LivingEntity le && (
                    (le.hasStatusEffect(StatusEffects.DARKNESS) && !SETTINGS.applyToDarkessFog) ||
                    (le.hasStatusEffect(StatusEffects.BLINDNESS) && !SETTINGS.applyToBlindnessFog)
            )) return;

            if (cameraSubmersionType == CameraSubmersionType.LAVA && !SETTINGS.applyToLavaFog) return;
            if (cameraSubmersionType == CameraSubmersionType.WATER && !SETTINGS.applyToWaterFog) return;
            if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW && !SETTINGS.applyToSnowFog) return;
            if (cameraSubmersionType == CameraSubmersionType.ATMOSPHERIC && !SETTINGS.applyToAtmosphericFog) return;

            if (clientWorld.getRegistryKey() == World.NETHER && !SETTINGS.applyToNetherFog) return;
            if (clientWorld.getRegistryKey() == World.END && !SETTINGS.applyToEndFog) return;
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
