#version 120		//Texture VertexShader

varying vec3 position;
varying vec3 normal;
varying vec4 color;
varying vec2 uv;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 normalMatrix;

void main()
{
	normal = normalize(vec3(normalMatrix * vec4(gl_Normal, 1.0)));
	color = gl_Color;
	uv = gl_MultiTexCoord0.xy;
    
    gl_Position = projectionMatrix * modelViewMatrix * gl_Vertex; 
}
