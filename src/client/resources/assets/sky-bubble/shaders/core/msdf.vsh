#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

layout(location = 0) out vec2 texCoord0;
layout(location = 1) out vec4 vertexColor;

layout(std140) uniform DynamicTransforms
{
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};

layout(std140) uniform Projection
{
    mat4 ProjMat;
};

void main()
{
    gl_Position = ProjMat * vec4(Position, 1.0);
    texCoord0 = vec2(UV0.x, 1.0 - UV0.y);
    vertexColor = Color * ColorModulator;
}
