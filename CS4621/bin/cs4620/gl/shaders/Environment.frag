#version 120

// You May Use The Following Functions As RenderMaterial Input
// vec4 getDiffuseColor(vec2 uv)
// vec4 getNormalColor(vec2 uv)
// vec4 getSpecularColor(vec2 uv)
// veck getEnvironmetLight(veck dir)

// Lighting Information
const int MAX_LIGHTS = 16;
uniform int numLights;
uniform vec3 lightIntensity[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];

// Camera Information
uniform vec3 worldCam;
uniform float exposure;

varying vec4 worldPos;

void main() {
	vec3 V = normalize(worldPos.xyz - worldCam);
	
	gl_FragColor = getEnvironmentColor(V) * exposure;
}