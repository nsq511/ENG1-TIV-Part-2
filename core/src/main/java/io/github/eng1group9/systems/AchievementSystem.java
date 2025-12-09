package io.github.eng1group9.systems;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.Main;

public class AchievementSystem {
    public static final float logoSize = 32f;
    public static float rowHeight = logoSize + 10f; // The height of rows for rendering
    public static float colWidth = 300f; // The width of columns for rendering

    private static String missingTex = "missingTexture.png";

    private static HashMap<String, Achievement> achievements = new HashMap<>();

    public static void init() {
        // Achievement initialisation
        AchievementSystem.addAchievement(
                Main.ACH_LONGBOI,
                "You found Longboi!",
                1,
                Main.LONGBOIBONUSAMOUNT,
                "ach_longboi.png");

        AchievementSystem.addAchievement(
                Main.ACH_DEAN_CAPTURES,
                "Get caught by the dean 3 times...",
                3,
                Main.ACH_DEAN_CAPTURE_POINT_PUNISHMENT,
                "ach_dean_capture.png");

        AchievementSystem.addAchievement(
                Main.ACH_INVIS,
                "Become invisible",
                1,
                "ach_invis.png");

        AchievementSystem.addAchievement(
                Main.ACH_LOST,
                "Take over 4 minutes to escape",
                1,
                "ach_lost.png");

        AchievementSystem.addAchievement(
                Main.ACH_QUICK,
                "Escape in under a minute",
                1,
                50,
                "ach_quick.png");

        AchievementSystem.addAchievement(
                Main.ACH_BOOK,
                "Steal librarian's book",
                1,
                50,
                "ach_book.png");
    }

    /**
     * Gets the specified achievement
     * 
     * @param title The title of the achievement
     * @return The achievement with the given title or null if it does not exist
     */
    public static Achievement getAchievement(String title) {
        return achievements.get(title);
    }

    /**
     * Adds an achievement to the system
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     * @param scoreModifier            How to modify the score when this achievement
     *                                 is acquired
     * @param logo                     The name of the logo image texture stored in
     *                                 the /Assets/Achievements directory
     */
    public static void addAchievement(String title, String description, int requiredConditionsAmount,
            float scoreModifier, String logo) {
        if (!Gdx.files.internal("Achievements/" + logo).exists()) {
            logo = missingTex;
        } else {
            logo = "Achievements/" + logo;
        }
        Achievement achievement = new Achievement(title, description, requiredConditionsAmount, scoreModifier,
                new Texture(logo));
        achievements.put(title, achievement);
    }

    /**
     * Adds an achievement to the system with defualt missing texture for the logo
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     * @param scoreModifier            How to modify the score when this achievement
     *                                 is acquired
     */
    public static void addAchievement(String title, String description, int requiredConditionsAmount,
            float scoreModifier) {
        AchievementSystem.addAchievement(title, description, requiredConditionsAmount, scoreModifier, missingTex);
    }

    /**
     * Adds an achievement to the system with defualt missing texture for the logo
     * and no score modifier
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     */
    public static void addAchievement(String title, String description, int requiredConditionsAmount) {
        AchievementSystem.addAchievement(title, description, requiredConditionsAmount, 0, missingTex);
    }

    /**
     * Adds an achievement to the system with no score modifier
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     * @param logo                     The name of the logo image texture stored in
     *                                 the /Assets/Achievements directory
     */
    public static void addAchievement(String title, String description, int requiredConditionsAmount, String logo) {
        AchievementSystem.addAchievement(title, description, requiredConditionsAmount, 0, logo);
    }

    /**
     * Increments the number of conditions that have been completed for the given
     * achievment
     * 
     * @param achievementTitle The title of the achievement to increment
     * 
     * @return Whether the achievment has met all conditions to be completed
     */
    public static boolean incAchievement(String achievementTitle) {
        Achievement achievement = achievements.get(achievementTitle);
        if (achievement == null)
            return false;
        return achievement.incConditions();
    }

    /**
     * Resets all achievments for a new game
     */
    public static void reset() {
        for (Achievement a : achievements.values()) {
            a.reset();
        }
    }

    public static float modifyScore(float score) {
        for (Achievement a : achievements.values()) {
            score = (int) a.modifyScore(score);
        }
        return score;
    }

    /**
     * Draws the given achievements in a grid if they have been achieved
     * Must be called within a batch begin/end
     * 
     * @param pos   The position to draw the grid at. Anchored at the top left
     * @param cols  The number of columns in the grid. The rows will be determined
     *              based on the size of achievements
     * @param batch The batch to use for drawing achievement logos
     * @param font  The font to use for drawing text
     */
    public static void draw(Vector2 pos, int cols, SpriteBatch batch, BitmapFont font) {
        Achievement[] achievementsArray = achievements.values().toArray(new Achievement[0]);
        final float buffer = 10f;

        int i = 0;
        for (Achievement achievement : achievementsArray) {
            if (!achievement.isAchieved())
                continue;

            int col = i % cols;
            int row = i / cols;

            // Place cursor in the bottom left of the cell
            float curX = pos.x + (col * (colWidth + buffer));
            float curY = (pos.y - rowHeight) - (row * rowHeight);

            achievement.render(new Vector2(curX, curY), batch, font);
            i++;
        }
    }
}

/**
 * Class for achievemnts in the game
 */
class Achievement {
    private Sprite logo; // Achievement logo for displaying to user
    private String name; // Title of achievement
    private String description; // What the achievement is for
    private boolean achieved;
    private float scoreModifier; // How to modify the score when the achievement is acquired

    private final int numReqConditions; // The number of conditions that must be met to acquire this achievement
    private int reqConditionCount; // The number of conditions the user has completed. This

    /**
     * Creates an achievement
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     * @param scoreModifier            How to modify the score when this achievement
     *                                 is acquired
     * @param logo                     The logo to use for this achievement
     */
    public Achievement(String title, String description, int requiredConditionsAmount, float scoreModifier,
            Texture logo) {
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = scoreModifier;
        achieved = false;
        this.logo = new Sprite(logo);
        this.logo.setSize(AchievementSystem.logoSize, AchievementSystem.logoSize);
    }

    /**
     * Creates an achievement with no score modifier
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     * @param logo                     The logo to use for this achievement
     */
    public Achievement(String title, String description, int requiredConditionsAmount, Texture logo) {
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = 0;
        achieved = false;
        this.logo = new Sprite(logo);
        this.logo.setSize(AchievementSystem.logoSize, AchievementSystem.logoSize);
    }

    /**
     * Creates an achievement with no texture
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     * @param scoreModifier            How to modify the score when this achievement
     *                                 is acquired
     */
    public Achievement(String title, String description, int requiredConditionsAmount, float scoreModifier) {
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = scoreModifier;
        achieved = false;
        logo = new Sprite();
        this.logo.setSize(AchievementSystem.logoSize, AchievementSystem.logoSize);
    }

    /**
     * Creates an achievement with no texture and no score modifier
     * 
     * @param title                    The name of the achievment
     * @param description              What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to
     *                                 acquire this achievement
     */
    public Achievement(String title, String description, int requiredConditionsAmount) {
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = 0;
        achieved = false;
        logo = new Sprite();
        this.logo.setSize(AchievementSystem.logoSize, AchievementSystem.logoSize);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Increments the number of conditions that have been completed
     * 
     * @return Whether the achievment has been achieved on this increment call
     */
    public boolean incConditions() {
        if (reqConditionCount < numReqConditions)
            reqConditionCount++;

        if (reqConditionCount == numReqConditions) {
            achieved = true;
            return true;
        }

        return false;
    }

    /**
     * Whether the achievement has been completed
     * 
     * @return Whether the achievement has been completed
     */
    public boolean isAchieved() {
        return achieved;
    }

    /**
     * Resets the achievement for a new game
     */
    public void reset() {
        achieved = false;
        reqConditionCount = 0;
    }

    /**
     * Modifies the score
     * 
     * @param score The score to modify
     * @return The modified score
     */
    public float modifyScore(float score) {
        return score + scoreModifier;
    }

    /**
     * Draws the logo with its title and desctiption.
     * Must be called within a batch begin/end
     * 
     * @param pos   The position to draw the achievement. Anchored at the bottom
     *              left
     * @param batch The sprite batch to use to draw the logo
     * @param font  The font to use to write text
     */
    public void render(Vector2 pos, SpriteBatch batch, BitmapFont font) {
        final float buffer = 10f;

        // Draw logo
        logo.setPosition(pos.x, pos.y);
        logo.draw(batch);

        float scale = font.getScaleX();
        // Draw text
        pos.x += logo.getWidth() + buffer;
        pos.y += logo.getHeight();
        font.getData().setScale(1.5f * scale);
        font.draw(batch, name, pos.x, pos.y);
        pos.y -= logo.getHeight() * 0.6f;
        font.getData().setScale(scale);
        font.draw(batch, description, pos.x, pos.y);
    }
}