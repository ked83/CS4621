#version 120

uniform sampler2d unTexColor;
uniform sampler2d unTexDepth;

varying vec2 vCoord;

void main() {
	float A = texture2D(unTexDepth, vec2(vCoord.x-1,vCoord.y+1);
	float B = texture2D(unTexDepth, vec2(vCoord.x,vCoord.y+1);
	float C = texture2D(unTexDepth, vec2(vCoord.x+1,vCoord.y+1);
	float D = texture2D(unTexDepth, vec2(vCoord.x-1,vCoord.y);
	float E = texture2D(unTexDepth, vec2(vCoord.x+1,vCoord.y);
	float F = texture2D(unTexDepth, vec2(vCoord.x-1,vCoord.y-1);
	float G = texture2D(unTexDepth, vec2(vCoord.x,vCoord.y-1);
	float H = texture2D(unTexDepth, vec2(vCoord.x+1,vCoord.y-1);
	
	float g = (abs(A+2*B+C-F-2*G-H)+abs(C+2*E+H-A-2*D-F))/8.0;

	gl_FragColor = texture2D(unTexDepth, vCoord); 
}
