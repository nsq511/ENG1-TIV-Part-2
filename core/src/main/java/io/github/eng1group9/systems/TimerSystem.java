package io.github.eng1group9.systems;

import java.sql.Date;

import com.badlogic.gdx.Gdx;

import io.github.eng1group9.Main;

/**
 * System used to keep track of how much time the player has left.
 */
public class TimerSystem {
    public static float elapsedTime = 0f;// time passed in seconds;
    private float timeToAdd;
    private static float TIMESTARTVALUE;

    public TimerSystem(float timeLimit) {
        elapsedTime = 0f;
        timeToAdd = 0f;
        TIMESTARTVALUE = timeLimit;
    }

    public void add(float amount) {
        elapsedTime += amount;
    }

    /**
     * Restarts the timer
     */
    public void reset(){
        elapsedTime = 0f;
        timeToAdd = 0f;
    }

    /**
     * Add to the timer, so that it goes up by this amount over time
     * @param amount
     */
    public void addGradually(float amount) {
        timeToAdd += amount;
    }

    /**
     * Update the timer.
     */
    public void tick() {
        float delta = Gdx.graphics.getDeltaTime();
        elapsedTime += delta + getExtraTime(delta);
        if (getTimeLeft() <= 0) {
            Main.LoseGame();
        }
    }

    /**
     * @return How much time should be added to the timer as a result of the addGradually method. 
     */
    private float getExtraTime(float delta) {
        if (timeToAdd <= 0) return 0;
        float change = delta * 25;      // Upper bound to how much the time can change in any given instant
        if (timeToAdd < change) change = timeToAdd;
        timeToAdd -= change;
        return change;
    }

    /**
     * @return How much time the player has left to escape.
     */
    public static int getTimeLeft() {
        return (int)(TIMESTARTVALUE - elapsedTime);
    }

    public static String getClockDisplay() {
        int timeLeft = getTimeLeft();
        String mins = getMinsDisplay(timeLeft);
        String secs = getSecsDisplay(timeLeft);
        return "Time Left: " + mins + ":" + secs;
        
    }
    
    private static String getMinsDisplay(int seconds) {
        return Integer.toString(getMins(seconds));
    }

    private static int getMins(int seconds) {
        return Math.floorDiv(seconds, 60);
    }

    private static String getSecsDisplay(int timeLeft) {
        int secsValue = getSecs(timeLeft);
        if (secsValue < 10) return "0" + Integer.toString(secsValue);
        return Integer.toString(secsValue);
    }

    private static int getSecs(int timeLeft) {
        return timeLeft - (getMins(timeLeft) * 60);
    }
}
