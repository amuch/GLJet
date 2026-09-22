#version 300 es

uniform mat4 uMVPMatrix;
in vec3 vPosition;

out vec4 varyingColor;

void main(void) {
    gl_Position = uMVPMatrix * vec4(vPosition, 1.0);
    varyingColor = vec4(vPosition, 1.0) * 0.5 + vec4(0.5, 0.5, 0.5, 1.0);
}