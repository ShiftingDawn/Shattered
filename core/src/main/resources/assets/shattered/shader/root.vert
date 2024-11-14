uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 globalTransformMatrix = mat4(1.0);

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 uv;

out DATA {
    vec4 color;
    vec2 uv;
} data;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * globalTransformMatrix * vec4(position, 1.0);
    data.color = color;
    data.uv = uv;
}