uniform bool enableTextures = false;
uniform sampler2D texture1;

in DATA {
    vec4 color;
    vec2 uv;
} data;

out vec4 outColor;

void main() {
    if (!enableTextures) {
        outColor = data.color;
    } else {
        outColor = data.color * texture(texture1, data.uv);
    }
}