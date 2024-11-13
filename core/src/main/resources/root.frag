#version 330 core

in DATA {
    vec4 color;
} data;

out vec4 outColor;

void main() {
    outColor = data.color;
}