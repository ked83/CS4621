#version 120

// You May Use The Following Functions As RenderMaterial Input
// vec4 getDiffuseColor(vec2 uv)
// vec4 getNormalColor(vec2 uv)
// vec4 getSpecularColor(vec2 uv)

// Lighting Information
const int MAX_LIGHTS = 16;
uniform int numLights;
uniform vec3 lightIntensity[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 ambientLightIntensity;

// Camera Information
uniform vec3 worldCam;
uniform float exposure;

// Shading Information
// 0 : smooth, 1: rough
uniform float roughness;

varying vec2 fUV;
varying vec3 fN;
varying vec4 worldPos;

void main()
{
	vec3 N = normalize(fN);
	vec3 V = normalize(worldCam - worldPos.xyz);
    float Fo = 0.04;
    float PI = 3.1415926535897932384626433832795;
    vec4 Ia = vec4(ambientLightIntensity, 0.0);
	
	vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);

	for (int i = 0; i < numLights; i++) {
	  float r = length(lightPosition[i] - worldPos.xyz);
	  vec3 L = normalize(lightPosition[i] - worldPos.xyz); 
	  vec3 H = normalize(L + V);
	  float F = Fo + (1 - Fo) * pow((1 - dot(V, H)), 5.0);
	  float ePow = exp((pow(dot(N, H), 2.0)-1)/(pow(roughness, 2.0) * pow(dot(N, H), 2.0)));
	  float D = (1/(pow(roughness, 2.0) * pow(dot(N, H), 4.0))) * ePow;
	  float G = min(1, min((2*dot(N, H)*dot(N, V))/dot(V, H), (2*dot(N, H)*dot(N, L))/dot(V, H)));
	  vec4 I = vec4(lightIntensity[i], 0.0);
	  vec4 Kd = getDiffuseColor(fUV);
	  vec4 Ks = getSpecularColor(fUV);
	  
	  vec4 A = (Ks*F*D*G)/(PI*dot(N, V)*dot(N, L)) + Kd;
	  vec4 B = max(dot(N, L), 0)*I/(r*r);
	  vec4 C = Kd * Ia;

	  finalColor += A * B + C;
	}

	gl_FragColor = finalColor * exposure; 
}