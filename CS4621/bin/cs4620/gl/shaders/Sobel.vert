#version 120

attribute vec2 vPosition; // Sem (POSITION 0)

varying vec2 vCoord;

void main() {
    vCoord = vPosition;
	gl_Position = vPosition;
}