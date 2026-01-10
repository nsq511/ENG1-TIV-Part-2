package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.Main;
import io.github.eng1group9.systems.RenderingSystem;
import io.github.eng1group9.systems.ToastSystem;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles everything connected to the player.
 *
 * @param startPos - The players start positon.
 * @param speed    - How fast the player will move.
 */
public class Player extends MovingEntity {

    final static Color BAD = new Color(1, 1, 0, 1);
    final static Color GOOD = new Color(0, 1, 1, 1);

    private boolean hasExitKey = false;
    private boolean hasChestRoomKey = false;
    private boolean hasRedPotion = false;
    private boolean hasLockpick = false;
    // I am getting a warning on the next line but an error if I remove it...
    private boolean defeatedBoss = false;
    private boolean hasFirestarter = false;
    private boolean hasMoney = false;
    private boolean hasStaff = false;
    private boolean hasJanitorKey = false;
    private List<Integer> booksRead = new LinkedList<>();
    private float invisibilityLeft = 0;
    private float invincibilityLeft = 0;
    private float potionDelay = 0;
    private float slownessLeft = 0;
    private int dashesLeft = 0;
    private float dashCooldown = 3;
    private boolean dashed = false;

    private boolean invisibilityWarningGiven = true;
    private int health;

    public Player(Vector2 startPos, float speed) {
        super(new Texture("Characters/playerAnimations.png"), new int[] { 4, 4, 4, 4, 4, 4, 4, 4 }, 32, 32, speed,
                startPos, new Vector2(16, 0), true, true);
        setScale(2);
    }

    public boolean hasFirestarter() {
        return hasFirestarter;
    }

    public boolean hasMoney() {
        return hasMoney;
    }

    public int booksRead() {
        return booksRead.size();
    }

    public boolean hasStaff() {
        return hasStaff;
    }

    public boolean hasJanitorKey() {
        return hasJanitorKey;
    }

    public void giveLockpick() {
        if (!hasLockpick) {
            ToastSystem.addToast("You found a lockpick!", GOOD);
            hasLockpick = true;
        }
    }

    public void giveFirestarter() {
        if (!hasFirestarter) {
            if (hasMoney) {
                ToastSystem.addToast("You bought a firestarter!", GOOD);
                ToastSystem.addToast("'Thank you for your purchase!'");
                hasFirestarter = true;
            } else {
                ToastSystem.addToast("You see a firestarter for sale, but you don't have any money!");
            }

        }
    }

    public void giveJanitorKey() {
        if (!hasJanitorKey) {
            ToastSystem.addToast("You found the janitor's key!", GOOD);
            hasJanitorKey = true;
        }

    }

    public void giveStaff() {
        if (!hasStaff) {
            RenderingSystem.hideLayer("Staff");
            int books = booksRead.size();

            ToastSystem.addToast("You picked up the staff...", GOOD);
            hasStaff = true;

            if (books < 3) {
                ToastSystem.addToast("But you don't know how to use it!", BAD);
            } else if (books < 7) {
                ToastSystem.addToast("If you finish your studies, you might be able to use it!", GOOD);
            } else {
                ToastSystem.addToast("It seems your studying has paid off! You can now use it on the boss!", GOOD);
            }
        }
    }

    public void giveMoney() {
        if (!hasMoney) {
            ToastSystem.addToast("You found a dinar in the bush!", GOOD);
            RenderingSystem.hideLayer("Coin");
            hasMoney = true;
        }
    }

    /**
     * Resets the Player to its original state
     */
    public void reset() {
        super.reset();
        health = 5;
        hasExitKey = false;
        hasChestRoomKey = false;
        hasRedPotion = false;
        hasLockpick = false;
        defeatedBoss = false;
        hasFirestarter = false;
        hasMoney = false;
        hasStaff = false;
        hasJanitorKey = false;
        booksRead = new LinkedList<>();
        invisibilityLeft = 0;
        invincibilityLeft = 0;
        slownessLeft = 0;
        dashesLeft = 0;
        dashCooldown = 3;
        potionDelay = 0;
        invisibilityWarningGiven = true;

    }

    public int getHealth() {
        return health;
    }

    public void damage() {
        if (health > 1) {
            health -= 1;
        } else {
            Main.LoseGame();
        }
    }

    /**
     * @return wether the player has the key to open the exit.
     */
    public boolean hasExitKey() {
        return hasExitKey;
    }

    /**
     *
     * @return if the player has the lockpick
     */
    public boolean hasLockpick() {
        return hasLockpick;
    }

    /**
     * Give the player the key to open the exit.
     */
    public void giveExitKey() {
        if (!hasExitKey) {
            hasExitKey = true;
            RenderingSystem.hideLayer("ExitKey");
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

    public void potionTriggered() {
        potionDelay = 3;
    }

    public float getPotionDelay() {
        return potionDelay;
    }

    /**
     *
     */
    public void readBook(int id) {
        if (!booksRead.contains(id)) {
            booksRead.add(id);
            ToastSystem.addToast("You read the book!");

            switch (id) {
                case 1:
                    ToastSystem.addToast("It contains hastily written lecture notes left behind by a student.");
                    break;
                case 2:
                    ToastSystem.addToast("It is a study of celestial storms, annotated by a student.");
                    break;
                case 3:
                    ToastSystem.addToast("It is an anatomical treatise on evil pacifist magic users.");
                    break;
                case 4:
                    ToastSystem.addToast("It is a heretical prayerbook with seemingly random annotations.");
                    break;
                case 5:
                    ToastSystem.addToast("It describes how to extract and store souls using evil pacifist magic.");
                    break;
                case 6:
                    ToastSystem.addToast("It describes how to enchant coffee using evil pacifist magic.");
                    break;
                case 7:
                    ToastSystem.addToast("It describes how to turn lead into gold using evil pacifist magic.");
                    break;
            }

            switch (booksRead.size()) {
                case 1:
                case 2:
                    ToastSystem.addToast("You don't understand what you are reading at all.", BAD);
                    break;
                case 3:
                    ToastSystem.addToast("It still doesn't make any sense to you.", BAD);
                    break;
                case 4:
                    ToastSystem.addToast("You can only understand a little bit of what you are reading.", BAD);
                    break;
                case 5:
                    ToastSystem.addToast("Some of the topics covered actually make a bit of sense.", GOOD);
                    break;
                case 6:
                    ToastSystem.addToast("For once, you feel as though you understand most of what you are reading.",
                            GOOD);
                    break;
                case 7:
                    ToastSystem.addToast(
                            "After reading the book, you finally have a basic understanding of evil pacifist magic!",
                            GOOD);
                    if (hasStaff) {
                        ToastSystem.addToast("You can now use your staff on the boss!", GOOD);
                    }
                    break;
            }
        }
    }

    /**
     * Move the player in the given direction
     * 
     * @param direction The direction to move (D = Down, U = Up, L = Left, R =
     *                  Right).
     */
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
        // System.out.println("Player Sprite Pos: " + Float.toString(getX()) + ", " +
        // Float.toString(getY()));
        // System.out.println("Player Sprite Size: " + Float.toString(getWidth()) + ", "
        // + Float.toString(getHeight()));
        // System.out.println(
        // "Hitbox Pos: " + Float.toString(getHitbox().getX()) + ", " +
        // Float.toString(getHitbox().getY()));
        // System.out.println("Hitbox Size: " + Float.toString(getHitbox().getWidth()) +
        // ", "
        // + Float.toString(getHitbox().getHeight()));
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
     * @return WHETHER the player is invincible after being damaged
     *
     */
    public boolean isInvincible() {
        return invincibilityLeft <= 0;
    }

    /**
     * Used to update the players invisiblity timer (could be used for more).
     */
    public void update() {

        // Invisibility Check
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

        // Invincible Check
        if (isInvincible()) {
            invincibilityLeft -= Gdx.graphics.getDeltaTime();
            if (!isInvincible()) {
                changeAnimation(1);
            }
        }

        // Slowness Check
        if (!(slownessLeft <= 0)) {
            slownessLeft -= Gdx.graphics.getDeltaTime();
            if (slownessLeft <= 0) {
                ToastSystem.addToast("Your slowness potion finally ran out!");
                setSpeed(100);
            }
        }

        // Dash Check
        if (dashed) {
            dashCooldown -= Gdx.graphics.getDeltaTime();
            if (dashCooldown <= 0) {
                dashed = false;
                dashCooldown = 3;
            }
        }

        if (potionDelay != 0) {
            potionDelay -= Gdx.graphics.getDeltaTime();
        }
    }

}
