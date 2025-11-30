package io.github.eng1group9.systems;

import java.util.Collection;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Class for achievemnts in the game
 */
public class Achievement {
    private static final float logoSize = 32f;
    public static float rowHeight = logoSize + 10f;    // The height of rows for rendering
    public static float colWidth = 260f;               // The width of columns for rendering

    private Sprite logo;            // Achievement logo for displaying to user
    private String name;            // Title of achievement
    private String description;     // What the achievement is for
    private boolean achieved;
    private float scoreModifier;    // How to modify the score when the achievement is acquired
    
    private final int numReqConditions;     // The number of conditions that must be met to acquire this achievement
    private int reqConditionCount;          // The number of conditions the user has completed. This 

    /**
     * Creates an achievement
     * 
     * @param title The name of the achievment 
     * @param description What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to acquire this achievement
     * @param scoreModifier How to modify the score when this achievement is acquired
     * @param logo The logo to use for this achievement
     */
    public Achievement(String title, String description, int requiredConditionsAmount, float scoreModifier, Texture logo){
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = scoreModifier;
        achieved = false;
        this.logo = new Sprite(logo);
        this.logo.setSize(logoSize, logoSize);
    }
    /**
     * Creates an achievement with no texture
     * 
     * @param title The name of the achievment 
     * @param description What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to acquire this achievement
     * @param scoreModifier How to modify the score when this achievement is acquired
     */
    public Achievement(String title, String description, int requiredConditionsAmount, float scoreModifier){
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = scoreModifier;
        achieved = false;
        logo = new Sprite();
        this.logo.setSize(logoSize, logoSize);
    }
    /**
     * Creates an achievement with no texture and no score modifier
     * 
     * @param title The name of the achievment 
     * @param description What the achievement is for
     * @param requiredConditionsAmount The number of conditions that must be met to acquire this achievement
     */
    public Achievement(String title, String description, int requiredConditionsAmount){
        numReqConditions = requiredConditionsAmount;
        reqConditionCount = 0;

        name = title;
        this.description = description;
        this.scoreModifier = 0;
        achieved = false;
        logo = new Sprite();
        this.logo.setSize(logoSize, logoSize);
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    /**
     * Increments the number of conditions that have been completed
     * 
     * @return Whether the achievment has met all conditions to be completed
     */
    public boolean incConditions(){
        reqConditionCount++;
        if(reqConditionCount >= numReqConditions){
            achieved = true;
        }
        return achieved;
    }

    /**
     * Whether the achievement has been completed
     * @return Whether the achievement has been completed
     */
    public boolean isAchieved(){
        return achieved;
    }

    /**
     * Resets the achievement for a new game
     */
    public void reset(){
        achieved = false;
        reqConditionCount = 0;
    }

    /**
     * Modifies the score
     * 
     * @param score The score to modify
     * @return The modified score
     */
    public float modifyScore(float score){
        return score + scoreModifier;
    }

    /**
     * Draws the logo with its title and desctiption.
     * 
     * @param pos The position to draw the achievement. Anchored at the bottom left
     * @param batch The sprite batch to use to draw the logo
     * @param font The font to use to write text
     */
    private void render(Vector2 pos, SpriteBatch batch, BitmapFont font){
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

    /**
     * Draws the given achievements in a grid if they have been achieved
     * 
     * @param pos The position to draw the grid at. Anchored at the top left
     * @param cols The number of columns in the grid. The rows will be determined based on the size of achievements
     * @param batch The batch to use for drawing achievement logos
     * @param font The font to use for drawing text
     * @param achievements The list of achievements to draw
     */
    public static void draw(Vector2 pos, int cols, SpriteBatch batch, BitmapFont font, Collection<Achievement> achievements){        
        Achievement[] achievementsArray = achievements.toArray(new Achievement[0]);
        final float buffer = 10f;

        int i = 0;
        for(Achievement achievement : achievementsArray){
            if(!achievement.isAchieved()) continue;

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