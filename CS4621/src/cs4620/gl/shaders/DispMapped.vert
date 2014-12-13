#version 120

// Note: We multiply a vector with a matrix from the left side (M * v)!
// mProj * mView * mWorld * pos

// RenderCamera Input
uniform mat4 mViewProjection;

// RenderObject Input
uniform mat4 mWorld;
uniform mat3 mWorldIT;

// RenderMesh Input
attribute vec4 vPosition; // Sem (POSITION 0)
attribute vec3 vNormal; // Sem (NORMAL 0)
attribute vec2 vUV; // Sem (TEXCOORD 0)

// Shading Information
uniform float dispMagnitude;

varying vec2 fUV;
varying vec3 fN;
varying vec4 worldPos;

void main() {
	vec4 h = getNormalColor(vUV);
	vec3 ho =  vec3(h.x, h.y, h.z);
	float mag = (ho.x + ho.y + ho.z)/3;
	vec4 newPos = vPosition + vec4((mag * dispMagnitude) * vNormal, 0.0);
	
	worldPos = mWorld * newPos;

	gl_Position = mViewProjection * worldPos;

	fN = normalize((mWorldIT * vNormal).xyz);
	fUV = vUV;
}
