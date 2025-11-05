package io.github.eng1group9.entities;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Dean extends MovingEntity {
    private int reach = 3; // size of dean hitbox in tiles (3x3)
    private int moveNum = 0;
    private int stepsLeft = 32;
    private Character[] path;
    private Rectangle reachRectangle;

    public Dean(Vector2 startPos, float speed, Character[] path) {
        super(new Texture("Characters/deanAnimations.png"), new int[] {4, 4,4,4} , 32, 32, speed);
        setPosition(startPos);
        setScale(2);

        Rectangle reachHitbox = new Rectangle();

        reachRectangle = new Rectangle();
        reachRectangle.setCenter(startPos);
        reachRectangle.setSize(reach * 32);

        this.path = path;
    }

    public void nextMove(List<Rectangle> worldCollision) {
        if (!isFrozen()) {
            Character direction = getNextDirection();
             move(direction, worldCollision);
             updateAnimation(direction);
             stepsLeft--;
             if (stepsLeft == 0) {
                stepsLeft = 32;
                moveNum++;
             }
        }
    }

    private Character getNextDirection() {
        if (moveNum >= path.length) {
            moveNum = 0;
        }
        return path[moveNum];
    }

    private void updateAnimation(Character direction) {
        switch (direction) {
                case 'U':
                    changeAnimation(1);
                    break;
                case 'L':
                    changeAnimation(3);
                    break;
                case 'D':
                    changeAnimation(0);
                    break;
                case 'R':
                    changeAnimation(2);
                    break;
            }
    }


}
