attribute vec4 a_position;

uniform mat4 u_projTrans;

varying vec2 v_worldPos;

void main() {
    v_worldPos = a_position.xy;
    gl_Position = u_projTrans * a_position;
}
