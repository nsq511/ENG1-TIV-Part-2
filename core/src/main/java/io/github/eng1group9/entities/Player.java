package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.systems.RenderingSystem;
import io.github.eng1group9.systems.ToastSystem;

/**
 * Handles everything connected to the player.
 * 
 * @param startPos - The players start positon.
 * @param speed    - How fast the player will move.
 */
public class Player extends MovingEntity {

    private boolean hasExitKey = false;
    private boolean hasChestRoomKey = false;
    private boolean hasRedPotion = false;
    private float invisibilityLeft = 0;
    private float slownessLeft = 0;
    private int dashesLeft = 0;
    private float dashCooldown = 3;
    private boolean dashed = false;

    private boolean invisibilityWarningGiven = true;

    public Player(Vector2 startPos, float speed) {
        super(new Texture("Characters/playerAnimations.png"), new int[] { 4, 4, 4, 4, 4, 4, 4, 4 }, 32, 32, speed,
                startPos, new Vector2(16, 0));
        setScale(2);
    }

    /**
     * Resets the Player to its original state
     */
    public void reset() {
        super.reset();
        hasChestRoomKey = false;
        hasExitKey = false;
        hasRedPotion = false;
        invisibilityLeft = 0;
        slownessLeft = 0;
        dashesLeft = 0;
        dashCooldown = 3;
        invisibilityWarningGiven = true;
    }

    /**
     * @return wether the player has the key to open the exit.
     */
    public boolean hasExitKey() {
        return hasExitKey;
    }

    /**
     * Give the player the key to open the exit.
     */
    public void giveExitKey() {
        if (!hasExitKey) {
            hasExitKey = true;
            ToastSystem.addToast("You found the Exit Key!");
        }
    }

    /**
     * @return wether the player has the key to open the room with the chest.
     */
    public boolean hasChestRoomKey() {
        return hasChestRoomKey;
    }

    /**
     * Give the player the key to open the room with the chest.
     */
    public void giveChestRoomKey() {
        if (!hasChestRoomKey) {
            hasChestRoomKey = true;
            RenderingSystem.hideLayer("Key");
            ToastSystem.addToast("You found the Storage Room Key!");
        }
    }

    /**
     * Give the player the red potion they must give to LongBoi.
     */
    public void giveRedPotion() {
        if (!hasRedPotion) {
            hasRedPotion = true;
            RenderingSystem.hideLayer("Potion");
            ToastSystem.addToast("You found a Red Potion?");
        }
    }

    /**
     * @return wether the player has the potion for LongBoi.
     */
    public boolean hasRedPotion() {
        return hasRedPotion;
    }

    /**
     * Make the player invisible for 15s, so they cannot be spotted by the Dean.
     */
    public void becomeInvisible() {
        invisibilityLeft = 15;
        invisibilityWarningGiven = false;
    }

    public void slownessPotion() {
        slownessLeft = 15;
        setSpeed(50);
    }

    public void speedPotion() {
        dashesLeft = 10;
    }

    public float getDashes() {
        return dashesLeft;
    }

    public boolean hasDashed() {
        return dashed;
    }

    /**
     * Move the player in the given direction
     * 
     * @param direction The direction to move (D = Down, U = Up, L = Left, R =
     *                  Right).
     */
    @Override
    public float move(Character direction) {
        int animationOffset = 0;
        if (!isVisible())
            animationOffset = 4;
        switch (direction) {
            case 'U':
                changeAnimation(1 + animationOffset);
                break;
            case 'D':
                changeAnimation(0 + animationOffset);
                break;
            case 'L':
                changeAnimation(3 + animationOffset);
                break;
            case 'R':
                changeAnimation(2 + animationOffset);
                break;
        }
        System.out.println("Player Sprite Pos: " + Float.toString(getX()) + ", " + Float.toString(getY()));
        System.out.println("Player Sprite Size: " + Float.toString(getWidth()) + ", " + Float.toString(getHeight()));
        System.out.println(
                "Hitbox Pos: " + Float.toString(getHitbox().getX()) + ", " + Float.toString(getHitbox().getY()));
        System.out.println("Hitbox Size: " + Float.toString(getHitbox().getWidth()) + ", "
                + Float.toString(getHitbox().getHeight()));
        return super.move(direction);
    }

    public float dash(Character direction) {
        dashesLeft--;
        dashed = true;
        return super.dash(direction);
    }

    /**
     * @return Wether the player is visible (to the Dean).
     */
    public boolean isVisible() {
        return invisibilityLeft <= 0;
    }

    /**
     * Used to update the players invisiblity timer (could be used for more).
     */
    public void update() {

        if (!isVisible()) {
            invisibilityLeft -= Gdx.graphics.getDeltaTime();
            if (isVisible()) {
                ToastSystem.addToast("Your invisibility has run out!");
                changeAnimation(1);
            }

            if (invisibilityLeft <= 5 && !invisibilityWarningGiven) {
                ToastSystem.addToast("Your invisibility is about to run out!");
                invisibilityWarningGiven = true;
            }

        }
        if (!(slownessLeft <= 0)) {
            slownessLeft -= Gdx.graphics.getDeltaTime();
            if (slownessLeft <= 0) {
                ToastSystem.addToast("Your slowness potion finally ran out!");
                setSpeed(100);
            }
        }

        if (dashed) {
            dashCooldown -= Gdx.graphics.getDeltaTime();
            if (dashCooldown <= 0) {
                dashed = false;
                dashCooldown = 3;
            }
        }
    }

}
