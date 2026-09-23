#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec4 vertexColor;
layout(location = 0) out vec4 fragColor;

uniform sampler2D Sampler0;

void main()
{
    vec4 texColor = texture(Sampler0, texCoord0);
    fragColor = texColor * vertexColor;

    if (fragColor.a < 0.01)
    {
        discard;
    }
}