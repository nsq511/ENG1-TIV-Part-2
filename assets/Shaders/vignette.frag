#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_playerPos;   // player position in WORLD units
uniform float u_radius;     // world units
uniform float u_softness;   // world units
uniform float u_darkness;

varying vec2 v_worldPos;

void main() {
    float d = distance(v_worldPos, u_playerPos);
    float mask = smoothstep(u_radius, u_radius + u_softness, d);
    float a = mask * u_darkness;
    gl_FragColor = vec4(0.0, 0.0, 0.0, a);
}
