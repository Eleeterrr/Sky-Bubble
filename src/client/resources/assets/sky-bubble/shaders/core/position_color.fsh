#version 150

in vec4 vertexColor;

out vec4 FragColor;

void main()
{
    if (vertexColor.a < 0.001)
    {
        discard;
    }

    FragColor = vertexColor;
}