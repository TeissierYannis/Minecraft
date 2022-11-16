#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPosition;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPos;

    fragNormal = normalize(worldPos).xyz;
    fragPosition = worldPos.xyz;
    fragTextureCoord = textureCoord / 2.5;
}