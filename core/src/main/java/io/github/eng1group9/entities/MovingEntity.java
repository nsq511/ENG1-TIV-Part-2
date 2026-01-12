package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
<<<<<<< Updated upstream
import com.badlogic.gdx.math.Vector2;
=======
import io.github.eng1group9.Main;
>>>>>>> Stashed changes

/**
 * An animated entity which can move. This class handles speed and collision.
 * @param spriteSheetTexture - The texture contaning the frames laid out in a grid, which are then used to create the aniamtions.
 * @param frameNumbers - An array of integers which state how many frames are in each animation, values should match the number of sprites on each row of the spriteSheet.
 * @param tileWidth - How wide each tile in the SpriteSheet is in pixels.
 * @param tileHeight - How high each tile in the SpriteSheet is in pixels.
 * @param speed - How fast the entity will move.
 * @param active- If the entity is active on game start
 */
public class MovingEntity extends AnimatedEntity {

    private float speed = 0;
    private boolean frozen = false;
<<<<<<< Updated upstream
    private float initSpeed = 0f;
    private boolean initActive = true;
    private boolean active = true;
    private boolean canCollide = true;
    private float dash = 75;

    public MovingEntity(
            Texture spriteSheetTexture, int[] frameNumbers,
            int tileWidth, int tileHeight,
            float speed, Vector2 startPos,
            Vector2 hitboxOffset, boolean active, boolean canCollide
        ) {
        super(spriteSheetTexture, frameNumbers, tileWidth, tileHeight, startPos, hitboxOffset);
=======

    public MovingEntity(Texture spriteSheetTexture, int[] frameNumbers, int tileWidth, int tileHeight, float speed) {
        super(spriteSheetTexture, frameNumbers, tileWidth, tileHeight);
>>>>>>> Stashed changes
        this.speed = speed;
        initSpeed = speed;
        initActive = active;
        frozen = false;
        this.active = active;
        this.canCollide = canCollide;
    }

    public MovingEntity(
            Texture spriteSheetTexture, int[] frameNumbers,
            int tileWidth, int tileHeight,
            float speed, Vector2 startPos,
            Vector2 hitboxOffset
        ) {
        this(spriteSheetTexture, frameNumbers, tileWidth, tileHeight, speed, startPos, hitboxOffset, true, true);

    }

    /**
<<<<<<< Updated upstream
     * Resets the MovingEntity to its original state
     */
    public void reset() {
        super.reset();
        speed = initSpeed;
        frozen = false;
        if(initActive){
            activate();
        }
        else{
            deactivate();
        }
    }

    /**
     * Update the speed of the entity.
     *
=======
     * Update the speed of the entity.
>>>>>>> Stashed changes
     * @param newSpeed - How fast the entity will now move.
     */
    public void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    /**
     * @return The speed of the entity.
     */
    public float getSpeed() {
        return speed;
    }

    /**
<<<<<<< Updated upstream
     * Moves the entity in a given direction provided it wont collide with anything.
     *
     * @param direction           The direction as either 'U' 'D' 'L' or'R'
     * @param collisionRectangles A list of rectangles which the entity cannot move
     *                            into.
=======
     * Moves the entity in a given direction provided it wont collide with anything and .
     * @param direction The direction as either 'U' 'D' 'L' or'R'
     * @param collisionRectangles A list of rectangles which the entity cannot move into.
>>>>>>> Stashed changes
     */
    public float move(Character direction) {
        if (!frozen) {
            float delta = Gdx.graphics.getDeltaTime();
            if (delta > 0.1)
                delta = 0;
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
                    newX -= distance;
                    break;
                case 'R':
                    newX += distance;
                    break;
            }
<<<<<<< Updated upstream
            if (io.github.eng1group9.Main.collisionSystem.safeToMove(newX, newY, getHitbox()) || !canCollide) {
=======
            if (Main.collisionSystem.safeToMove(newX, newY, getHitbox())) {
>>>>>>> Stashed changes
                setPosition(newX, newY);
                return distance;
            }
        }
        return 0;
    }

<<<<<<< Updated upstream
    public float dash(Character direction) {
        if (!frozen) {
            float newX = getX();
            float newY = getY();
            switch (direction) {
                case 'U':
                    newY += dash;
                    break;
                case 'D':
                    newY -= dash;
                    break;
                case 'L':
                    newX -= dash;
                    break;
                case 'R':
                    newX += dash;
                    break;
            }
            if (io.github.eng1group9.Main.collisionSystem.safeToMove(newX, newY, getHitbox())) {
                setPosition(newX, newY);
                return dash;
            }
        }
        return 0;
    }
=======
>>>>>>> Stashed changes

    /**
     * Prevent the entity from moving, and pause their animation.
     */
    public void freeze() {
        frozen = true;
        pauseAnimation();
    }

    /**
     * Allow the entity to move, and resume their animation.
     */
    public void unfreeze() {
        frozen = false;
        playAnimation();
    }

    /**
     * @return Wether the entity is frozen.
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Makes the entity visible and unfreezes it, as well as whatever is defined in the inherited entity class
     */
    public void activate(){
        active = true;
        unfreeze();
    }

    /**
     * Hides the entity and freezes it, as well as whatever is defined in the inherited entity class
     */
    public void deactivate(){
        active = false;
        freeze();
    }

    /**
     * @return whether the entity is active
     */
    public boolean isActive() {
        return active;
    }
}
