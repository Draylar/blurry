package dev.draylar.blurry;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.draylar.blurry.config.BlurryConfig;
import dev.draylar.blurry.mixin.ShaderEffectAccessor;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.util.Identifier;

public class Blurry implements ClientModInitializer {

    public static final BlurryConfig CONFIG = OmegaConfig.register(BlurryConfig.class);
    public static final Identifier BLUR_SHADER_ID = new Identifier("shaders/post/blur_ui.json");
    public static ShaderEffect blurShader;

    public static void render(float tickDelta, float fadeProgress) {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.resetTextureMatrix();

        for (PostProcessShader pass : ((ShaderEffectAccessor) Blurry.blurShader).getPasses()) {
            pass.getProgram().getUniformByNameOrDummy("FadeProgress").set(fadeProgress);
        }

        blurShader.render(tickDelta);
    }

    @Override
    public void onInitializeClient() {

    }
}
