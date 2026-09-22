#version 300 es

uniform mat4 uMVPMatrix;

in vec4 vPosition;
in vec2 aTexCoord;

out vec2 vTexCoord;

void main() {
    gl_Position = uMVPMatrix * vPosition;
    vTexCoord = aTexCoord;
}