package dev.draylar.blurry.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;

public class BlurryConfig implements Config {

    @Comment("The speed blur progresses at each tick. Higher is faster.")
    public float blurSpeed = 0.15f;

    @Override
    public String getName() {
        return "blurry";
    }

    @Override
    public String getExtension() {
        return "json5";
    }
}
