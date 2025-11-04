package io.github.eng1group9.entities;

import java.util.List;

import io.github.eng1group9.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles everything connected to the player.
 * @author Mat and Max.
 */
public class Player extends AnimatedEntity {

    private float speed = 100;
    private boolean hasExitKey = false;
    private boolean hasChestRoomKey = false;

    public Player(Vector2 startPos) {
        super(new Texture("Characters/playerAnimations.png"), new int[] {4, 4,4,4} , 32, 32);
        setPosition(startPos);
        setScale(2);
    }


    public void handleInputs(List<Rectangle> worldCollision) {
        if (!isFrozen()) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                move('U', worldCollision);
                changeAnimation(1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                move('L', worldCollision);
                changeAnimation(3);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                move('D', worldCollision);
                changeAnimation(0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                move('R', worldCollision);
                changeAnimation(2);
            }
        }
    }

    public void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    public float getSpeed() {
        return speed;
    }

    public void move(char direction, List<Rectangle> collisionRectangles){
        float delta = Gdx.graphics.getDeltaTime();
        float distance = delta * speed;
        float newX = getX();
        float newY = getY();
        switch (direction) {
            case 'U':
                newY += distance;
                break;
            case 'D':
                newY -= distance;
                break;
            case 'L':
                newX-= distance;
                break;
            case 'R':
                newX += distance;
                break;
        }
        if (safeToMove(newX, newY, collisionRectangles)) {
                setPosition(newX, newY);
        }
    }


    private boolean safeToMove(float x, float y, List<Rectangle> collisionRectangles) {
        Rectangle testHitbox = new Rectangle();
        testHitbox = testHitbox.set(getHitbox());
        testHitbox.setPosition(x + 16, y + 16);

        for (Rectangle rectangle : collisionRectangles) {
            if (rectangle.overlaps(testHitbox)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasExitKey() {
        return hasExitKey;
    }

    public void setHasExitKey(Boolean bool) {
        System.out.println("Got exit key");
        hasExitKey = bool;
    }
    public boolean hasChestRoomKey() {
        return hasChestRoomKey;
    }

    public void setHasChestRoomKey(Boolean bool) {
        if (bool) {
            System.out.println("Got key");
            hasChestRoomKey = bool;
            Main.instance.deleteKeyTile();
        }
    }
}
