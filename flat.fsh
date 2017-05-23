#version 330   // Mac OS/X and Windows (probably)
//#version 130   // Linux
/**
 * flat fragment shader
 * color comes from program
 */

in vec4 color;
uniform vec4 vColor;

out vec4 fcolor;   // only one out, it will be in position 0

void main()
{
    fcolor = color; // gets color from vertex shader
    fcolor = vColor;
}
