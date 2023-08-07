#version 330

in vec2 outTextCoord;

out vec4 fragColor;

uniform sampler2D txtSampler;

void main()
{
    fragColor = texture(txtSampler, outTextCoord);
//    fragColor = vec4(1, 1, 1, 0);
}