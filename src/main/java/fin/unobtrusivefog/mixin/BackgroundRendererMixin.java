package fin.unobtrusivefog.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fin.unobtrusivefog.FogRenderType;
import fin.unobtrusivefog.Main;
import fin.unobtrusivefog.Settings;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Unique
    private static final Settings SETTINGS = Main.settings;

    @Inject(
            method = "applyFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"
            )
    )
    private static void modifyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Local CameraSubmersionType cameraSubmersionType, @Local Entity entity, @Local BackgroundRenderer.FogData fogData) {

        // peak code writing here
        if (!SETTINGS.applyToAll) {
            World world = entity.getWorld();

            if (
                (entity instanceof LivingEntity le && (
                        (!SETTINGS.applyToDarkessFog && le.hasStatusEffect(StatusEffects.DARKNESS)) ||
                        (!SETTINGS.applyToBlindnessFog && le.hasStatusEffect(StatusEffects.BLINDNESS))
                )) ||

                (!SETTINGS.applyToLavaFog && cameraSubmersionType == CameraSubmersionType.LAVA) ||
                (!SETTINGS.applyToWaterFog && cameraSubmersionType == CameraSubmersionType.WATER) ||
                (!SETTINGS.applyToSnowFog && cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) ||

                (!SETTINGS.applyToNetherFog && world.getRegistryKey() == World.NETHER) ||
                (!SETTINGS.applyToEndFog && world.getRegistryKey() == World.END) ||
                (!SETTINGS.applyToOverworld && world.getRegistryKey() == World.OVERWORLD)
            ) return;
        }

        float fogEnd = viewDistance;

        if (SETTINGS.renderDistanceFogType == FogRenderType.DISABLED) {
            fogData.fogEnd = Integer.MAX_VALUE;
        } else if (SETTINGS.renderDistanceFogType == FogRenderType.UNOBTRUSIVE && fogData.fogEnd < fogEnd) {
            fogData.fogEnd = fogEnd;
        }
    }
}
