package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BossProjectile extends MovingEntity{

    private final Character direction;
    public float warningLength;

    public BossProjectile(Vector2 position, Character direction, float speed, float warningLength) {
        super(new Texture("Projectile/projectile.png"), new int[] {1} , 32, 32, speed, new Vector2(150,150), position, false, false);
        this.direction = direction;
        setHitbox(new Rectangle());
        this.warningLength = warningLength;
        setScale(1);
        setX(position.x);
        setY(position.y);
        System.out.println("Projectile spawned at: " + position);
    }

    public void nextMove(){
        float delta = Gdx.graphics.getDeltaTime();
        warningLength -= delta;
        if(warningLength <= 0){
            this.activate();
            move(direction);
        }
    }

    public boolean hittingPlayer(Player player){
        return player.isColliding(getHitbox());
    }
}
