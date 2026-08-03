#version 150

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

uniform sampler2D Sampler0;

const float pxRange = 4.0;

float median(float r, float g, float b)
{
    return max(min(r, g), min(max(r, g), b));
}

float getScreenPxRange()
{
    vec2 texSize = vec2(textureSize(Sampler0, 0));
    vec2 unitRange = vec2(pxRange) / texSize;
    vec2 screenTexSize = vec2(1.0) / fwidth(texCoord0);
    return max(0.5 * dot(unitRange, screenTexSize), 1.0);
}

float sampleMsdf(vec2 uv)
{
    vec3 msd = texture(Sampler0, uv).rgb;
    return median(msd.r, msd.g, msd.b);
}

void main()
{
    float screenPxRange = getScreenPxRange();
    float opacity;

    if (screenPxRange < 2.5)
    {
        vec2 dx = dFdx(texCoord0) * 0.25;
        vec2 dy = dFdy(texCoord0) * 0.25;

        float s0 = sampleMsdf(texCoord0 + dx + dy);
        float s1 = sampleMsdf(texCoord0 - dx + dy);
        float s2 = sampleMsdf(texCoord0 + dx - dy);
        float s3 = sampleMsdf(texCoord0 - dx - dy);

        float sd0 = screenPxRange * (s0 - 0.5) + 0.5;
        float sd1 = screenPxRange * (s1 - 0.5) + 0.5;
        float sd2 = screenPxRange * (s2 - 0.5) + 0.5;
        float sd3 = screenPxRange * (s3 - 0.5) + 0.5;

        opacity = (clamp(sd0, 0.0, 1.0) + clamp(sd1, 0.0, 1.0) + clamp(sd2, 0.0, 1.0) + clamp(sd3, 0.0, 1.0)) * 0.25;
    }
    else
    {
        float sd = sampleMsdf(texCoord0);
        float screenPxDistance = screenPxRange * (sd - 0.5) + 0.5;
        opacity = clamp(screenPxDistance, 0.0, 1.0);
    }

    if (opacity < 0.001)
    {
        discard;
    }

    fragColor = vec4(vertexColor.rgb, vertexColor.a * opacity);
}