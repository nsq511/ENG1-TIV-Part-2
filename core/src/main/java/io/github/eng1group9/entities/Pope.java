package io.github.eng1group9.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Pope extends MovingEntity
{
    public Pope(Texture spriteSheetTexture, int[] frameNumbers, int tileWidth, int tileHeight, float speed, Vector2 startPos) {
        super(new Texture("Characters/popeAnimations.png"), new int[] {1}, 32, 32, speed, startPos);
    }

    public void release(){
        super.setPosition(500f,400f);
        super.move('U');
        super.move('U');
        super.move('U');
        super.move('U');
        super.move('U');
        super.move('U');
    }
}
