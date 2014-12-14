#version 120

// Lighting Information
const int MAX_LIGHTS = 16;
uniform int numLights;
uniform vec3 lightIntensity[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];

// Camera Information
uniform vec3 worldCam;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world coordinates

void main() {
	// interpolating normals will change the length of the normal, so renormalize the normal.
	vec3 N = normalize(fN);
	//view direction normal
	vec3 V = normalize(worldCam - worldPos.xyz);
	
	vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);
	float highlight = 1.0;
	float shadow = 0.9;
	float shadow2 = 0.7;
	
	float view = dot(N, V);
	
	if (view < .25) {
	    finalColor = vec4(0.0,0.0,0.0,1.0);
	  }
	
	else {
	  for (int i = 0; i < numLights; i++) {
	    float r = length(lightPosition[i] - worldPos.xyz);
	    //normal from point to light
	    vec3 L = normalize(lightPosition[i] - worldPos.xyz); 
	    
	    
	    if (r > 2) {
	      highlight = highlight * 0.8;
	      shadow = shadow * 0.8;
	      shadow2 = shadow2 * 0.8;
	    }
	    if (r > 4) {
	      highlight = highlight * 0.7;
	      shadow = shadow * 0.7;
	      shadow2 = shadow2 * 0.7;
	    }
	    if (r > 8) {
	      highlight = highlight * 0.55;
	      shadow = shadow * 0.55;
	      shadow2 = shadow2 * 0.55;
	    }
	    if (r > 16) {
	      highlight = highlight * 0.3;
	      shadow = shadow * 0.3;
	      shadow2 = shadow2 * 0.3;
	    }
  
	    // get diffuse color
	    vec4 diff = getDiffuseColor(fUV);
	    diff = clamp(diff, 0.0, 1.0);
	  
	    float intensity = dot(N, L);
	    
	    finalColor += clamp(vec4(lightIntensity[i], 0.0) * (diff), 0.0, 1.0);
	  
	    if (intensity > 0.9) {
	      finalColor = finalColor * highlight;
	    }
	    if (intensity < 0.9) {
	      finalColor = finalColor * shadow;
	    }
	  
	    if (intensity < 0) {
	      finalColor = finalColor * shadow2;
	    }
	    
	  }
	}

	gl_FragColor = finalColor; 
}
