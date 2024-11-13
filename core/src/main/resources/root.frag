#version 330 core

in DATA {
    vec4 color;
} inData;

out vec4 outColor;

void main() {
    outColor = vec4(1, 0, 0, 1);//inData.color;
}