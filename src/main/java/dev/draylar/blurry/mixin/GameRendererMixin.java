package dev.draylar.blurry.mixin;

import dev.draylar.blurry.Blurry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    private float prevFadeProgress = 0.0f;

    @Unique
    private float fadeProgress = 0.0f;

    @Inject(method = "tick", at = @At("HEAD"))
    private void blurry$tickBlur(CallbackInfo ci) {
        if(MinecraftClient.getInstance().currentScreen != null) {
            prevFadeProgress = fadeProgress;
            fadeProgress = Math.min(1.0f, fadeProgress + Blurry.CONFIG.blurSpeed);
        } else {
            prevFadeProgress = 0;
            fadeProgress = 0;
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;beginWrite(Z)V"))
    private void blurry$renderBlurredWorld(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if(MinecraftClient.getInstance().currentScreen != null) {
            double x = MathHelper.lerp(tickDelta, prevFadeProgress, fadeProgress);
            double eased = x < 0.5 ? 4 * x * x * x :
                    1 - Math.pow(-2 * x + 2, 3) / 2;

            Profiler profiler = MinecraftClient.getInstance().getProfiler();
            profiler.pop(); // "level"
            profiler.pop(); // "gameRenderer"
            profiler.push("Blurry");
            Blurry.render(tickDelta, (float) eased);
            profiler.pop();
            profiler.push("gameRenderer");
            profiler.push("level");
        } else {
            fadeProgress = 0.0f;
        }
    }

    @Inject(method = "reload", at = @At("RETURN"))
    private void blurry$loadShaders(ResourceManager manager, CallbackInfo ci) {
        if(Blurry.blurShader != null) {
            Blurry.blurShader.close();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        try {
            Blurry.blurShader = new ShaderEffect(client.getTextureManager(), manager, client.getFramebuffer(), Blurry.BLUR_SHADER_ID);
            Blurry.blurShader.setupDimensions(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Inject(method = "onResized", at = @At("RETURN"))
    private void blurry$resizeShaders(int width, int height, CallbackInfo ci) {
        if(Blurry.blurShader != null) {
            Blurry.blurShader.setupDimensions(width, height);
        }
    }
}
