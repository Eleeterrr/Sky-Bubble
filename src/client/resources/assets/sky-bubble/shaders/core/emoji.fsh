#version 150

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

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