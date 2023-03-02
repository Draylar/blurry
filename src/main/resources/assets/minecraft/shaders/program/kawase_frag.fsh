#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Scale;
uniform float FadeProgress;

in vec2 texCoord;

out vec4 fragColor;

vec2 texel = 1.0 / InSize;

vec4 kawase(sampler2D image, vec2 uv, vec2 resolution) {
    vec4 offset = texel.xyxy * vec4(-1, -1, 1, 1);

    vec4 sum = texture(image, uv) * 4.0;
    sum += texture(image, uv + offset.xy * Scale);
    sum += texture(image, uv + offset.xw * Scale);
    sum += texture(image, uv + offset.zy * Scale);
    sum += texture(image, uv + offset.zw * Scale);

    return sum / 8.0;
}

void main() {
    fragColor = mix(
        kawase(DiffuseSampler, texCoord, InSize.xy),
        texture(DiffuseSampler, texCoord),
        1.0 - FadeProgress
    );
}
