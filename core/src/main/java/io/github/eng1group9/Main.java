package io.github.eng1group9;

import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.entities.*;
import io.github.eng1group9.systems.InputSystem;
import io.github.eng1group9.systems.LeaderBoard;
import io.github.eng1group9.systems.RenderingSystem;
import io.github.eng1group9.systems.AchievementSystem;
import io.github.eng1group9.systems.CollisionSystem;
import io.github.eng1group9.systems.ToastSystem;
import io.github.eng1group9.systems.TriggerSystem;
import io.github.eng1group9.systems.TimerSystem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    final static int VIEWPORTWIDTH = 480;
    final static int VIEWPORTHEIGHT = 320;

    boolean isFullscreen = false;
    static int gameState = 0;
    /// 0 = Not started
    /// 1 = Playing
    /// 2 = Paused
    /// 3 = Win
    /// 4 = Lose

    public static float TIMERSTARTVALUE = 300f;
    public static boolean chestDoorOpen = false; // Whether the door to the chest room has been opened.
    public static boolean exitOpen = false; // Whether the exit is open/
    public static boolean spikesLowered = false; // Whether the spikes in the chest room have been lowered.
    public static boolean scrollUsed = false; // Whether the scroll power up has been collected.
    public static int longboiBonus = 0; // the bonus to add based on whether LongBoi was found.
    public static int hiddenEventCounter = 0;
    public static int negativeEventCounter = 0;
    public static int positiveEventCounter = 0;
    public static final String leaderBoardFilePath = "leaderboard.txt";

    public static LeaderBoard leaderBoard = LeaderBoard.loadFromFile(leaderBoardFilePath, 5);

    private static TimerSystem timerSystem = new TimerSystem(TIMERSTARTVALUE);
    public static boolean showCollision = false;

    private List<Rectangle> worldCollision;
    final static String TMXPATH = "World/testMap.tmx";
    
    public static Player player;
    private static boolean playerCaught = false; // Whether the player is currently being held by the Dean.
    final static float INITALPLAYERCAUGHTTIME = 1.2f;
    private static float playerCaughtTime = INITALPLAYERCAUGHTTIME; // how many seconds the Dean will hold the player when caught.
    final static Vector2 PLAYERSTARTPOS = new Vector2(16, 516); // Where the player begins the game, and returns to when caught.
    final float DEFAULTPLAYERSPEED = 100; // The players speed.

    private static Dean dean;
    final static Vector2 DEANSTARTPOS = new Vector2(32, 352); // Where the Dean begins the game, and returns to after catching the player.
    final float DEFAULTDEANSPEED = 100;
    final int DEAN_TIME_PUNISHMENT = 30; // The number of seconds the Dean adds to the timer.
    final static Character[] DEANPATH = { // The path the dean will take in the first room (D = Down, U = Up, L = Left, R = Right). The path will loop.
        'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
        'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
        'R', 'R', 'R',
        'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U',
        'L', 'L', 'L',
        'D', 'D', 'D', 'D',
        'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L'
    };
    
    final static Color BAD = new Color(1f,1f,0f,1f);
    final static Color GOOD = new Color(0f,1f,1f,1f);
    final static Color ACHIEVEMENT = new Color(1f, 0.7f, 0.1f, 1f);
    
    public static Main instance;
    public static RenderingSystem renderingSystem = new RenderingSystem();
    public static CollisionSystem collisionSystem = new CollisionSystem();
    public static InputSystem inputSystem = new InputSystem();
    
    // Achievement names
    public static final String ACH_LONGBOI = "Bird Spotter";
    public final static int LONGBOIBONUSAMOUNT = 50;
    public static final String ACH_DEAN_CAPTURES = "Troublemaker";
    public static final int ACH_DEAN_CAPTURE_POINT_PUNISHMENT = -20;      // The points lost for getting the dean capture achievement
    public static final String ACH_INVIS = "Now you see me...";
    public static final String ACH_LOST = "Detour Champion";
    public static boolean achTriggered = false;
    public static final String ACH_QUICK = "Fancy a Quickie?";

    @Override
    public void create() {
        renderingSystem.initWorld(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        collisionSystem.init(renderingSystem.getMapRenderer().getMap());
        TriggerSystem.init(TMXPATH);
        worldCollision = collisionSystem.getWorldCollision();
        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEANPATH);
        togglePause();
        instance = this;

        AchievementSystem.init();
    }

    /**
     * Initialises variables such as game states and entity positions. Used on initialisation and reset
     */
    public static void initSystem(){
        gameState = 0; // Not started

        chestDoorOpen = false;
        exitOpen = false;
        spikesLowered = false;
        scrollUsed = false;
        longboiBonus = 0;
        hiddenEventCounter = 0;
        negativeEventCounter = 0;
        positiveEventCounter = 0;
        timerSystem.reset();
        showCollision = false;
        playerCaught = false;
        playerCaughtTime = INITALPLAYERCAUGHTTIME;
        player.reset();
        dean.reset();
        RenderingSystem.reset();
        collisionSystem.reset();
        loadRoom(0,0, PLAYERSTARTPOS, DEANSTARTPOS, DEANPATH);
        AchievementSystem.reset();
        achTriggered = false;
    }

    @Override
    public void render() {
        inputSystem.handle(player);
        if (gameState == 1) logic();
        draw();
    }

    /**
     * Check if the player has the red potion.
     * If they do, show Long Boi and complete the hidden event.
     * If not, tell the player they must find the potion.
     */
    public static void checkForLongboi() {
        Color messageColour = new Color(0.2f, 1, 0.2f ,1);
        if (longboiBonus == 0 && !player.hasRedPotion()) {
            ToastSystem.addToast("Hello There! I seem to have misplaced my Red Potion, could you get it for me?", messageColour);
        }
        else if (longboiBonus == 0 && player.hasRedPotion()){
            ToastSystem.addToast("You found my potion! Thank you!", messageColour);
            RenderingSystem.showLayer("LONGBOI");
            hiddenEventCounter++;
            incAchievement(ACH_LONGBOI);    // Trigger longboi achievement
        }
    }

    /**
     * Adds an achievement toast
     * 
     * @param text The 
     */
    private static void achNotif(String achievementName){
        ToastSystem.addToast("Achievement: " + achievementName, ACHIEVEMENT);
    }
    
    /**
     * Increments an achievement and adds a toast message if this increment acquired the achievement
     * 
     * @param achievement_name The name of the achievment to increment
     * 
     * @return Whether the achievement was acquired in this increment
     */
    private static boolean incAchievement(String achievement_name){
        boolean ach_achieved = AchievementSystem.incAchievement(achievement_name);
        if(ach_achieved){
            achNotif(achievement_name);
        }
        return ach_achieved;
    }

    public void draw() {
        renderingSystem.draw(player, dean, showCollision, TimerSystem.elapsedTime, worldCollision);
        switch (gameState) {
            case 0:
                renderingSystem.renderStartOverlay(960, 640);
                break;
            case 2:
                renderingSystem.renderPauseOverlay(960, 640, positiveEventCounter, negativeEventCounter, hiddenEventCounter);
                break;
            case 3:
                renderingSystem.renderWinOverlay(960, 640, TimerSystem.getTimeLeft(), calculateScore(), positiveEventCounter, negativeEventCounter, hiddenEventCounter);
                break;
            case 4:
                renderingSystem.renderLoseOverlay(960, 640, positiveEventCounter, negativeEventCounter, hiddenEventCounter);
                break;
            default:
                break;
        }
    }

    /**
     * Open the door of the room with the chest.
     * Remove its hitbox and graphic.
     */
    public static void openChestRoomDoor() {
        if (player.hasChestRoomKey() && !chestDoorOpen) {
            ToastSystem.addToast("You Opened the Door!", GOOD);
            collisionSystem.removeCollisionByName("chestRoomDoor");
            RenderingSystem.hideLayer("ChestDoorClosed");
            chestDoorOpen = true;
        }
    }

    /**
     * Open the exit.
     * Remove its hitbox and hide its graphic.
     */
    public static void openExit() {
        if (player.hasExitKey() && !exitOpen) {
            ToastSystem.addToast("You Opened the Exit!", GOOD);
            collisionSystem.removeCollisionByName("exitDoor");
            RenderingSystem.hideLayer("ExitClosed");
            exitOpen = true;
        }
    }

    /**
     * Give the scroll powerup to the player, making the player invisible for 15s.
     * Hide the scroll graphic.
     */
    public static void getScroll() {
        if (!scrollUsed) {
            incAchievement(ACH_INVIS);      // Trigger invisible scroll achievement
            player.becomeInvisible();
            RenderingSystem.hideLayer("Scroll");
            scrollUsed = true;
            ToastSystem.addToast("You got the Scroll!", GOOD);
            ToastSystem.addToast("You are invisible for 15s", GOOD);
            positiveEventCounter++;
        }
    }

    /**
     * Set the game to the started state, and unpause it.
     * Used once to start the game.
     */
    public static void startGame() {
        if (gameState == 0) {
            gameState = 2;
            togglePause();
        }
        else if(gameState == 3 || gameState == 4){
            initSystem();
            gameState = 1;
        }
    }

    /**
     * Set the game to the win state, used when the player escapes.
     */
    public static void winGame() {
        togglePause();
        if(TimerSystem.elapsedTime <= 60){
            incAchievement(ACH_QUICK);      // Trigger quick finish achievement
        }
        gameState = 3;
    }

    /**
     * Set the game to the lose state, used when the player runs out of time.
     */
    public static void LoseGame() {
        togglePause();
        gameState = 4;
    }

    /**
     * Calculate the players score based on how much time was left and whether they found Long Boi.
     * @return The score.
     */
    public static int calculateScore() {
        int score = TimerSystem.getTimeLeft();
        score = (int)AchievementSystem.modifyScore(score);
        return score;
    }

    /**
     * Toggle whether the window should be in Windowed or Fullcreen mode.
     */
    public void toggleFullscreen() {
        if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 640);
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            isFullscreen = !isFullscreen;
    }

    /**
     * Toggle whether the game should be paused.
     * This will freeze the player/dean, stop all game logic and display the pause overlay.
     */
    public static void togglePause() {
        if (gameState == 2) {
            if (!playerCaught) {
                player.unfreeze();
                dean.unfreeze();
            }
            gameState = 1;
        }
        else {
            player.freeze();
            dean.freeze();
            if (gameState == 1) gameState = 2;
        }
    }

    /**
     * Where the game processes its logic each frame.
     * It will not run if the game is paused.
     */
    public void logic() {
        timerSystem.tick();
        
        if(!achTriggered && TimerSystem.elapsedTime > 4 * 60){
            achTriggered = incAchievement(ACH_LOST);         // Trigger slow finish achievement
        }

        dean.nextMove();
        checkDeanCatch();
        TriggerSystem.checkTouchTriggers(player);
        player.update();
    }

    /**
     * Checks if the dean has caught the player, and punishes them if he has by removing 50s from time left.
     */
    public void checkDeanCatch() {
        if (dean.canReach(player) && !playerCaught) {
            incAchievement(ACH_DEAN_CAPTURES);      // Increment count for Dean capture achievment
            startPlayerCatch();
        }
        else if (playerCaught) {
            if (playerCaughtTime <= 0) {
                endPlayerCatch();
                playerCaughtTime = INITALPLAYERCAUGHTTIME;
            }
            else {
                float delta = Gdx.graphics.getDeltaTime();
                playerCaughtTime -= delta;
            }
        }
    }

    /**
     * Run when the player is first caught by the Dean.
     * This will begin the sequence where the player is held in detention while the timer goes down.
     * This will freeze the player and dean.
     */
    private void startPlayerCatch() {
        playerCaught = true;
        player.freeze();
        player.setPosition(PLAYERSTARTPOS);
        dean.freeze();
        dean.changeAnimation(3);
        dean.setPosition(PLAYERSTARTPOS.x + 32, PLAYERSTARTPOS.y);
        timerSystem.addGradually(DEAN_TIME_PUNISHMENT - INITALPLAYERCAUGHTTIME);
        ToastSystem.addToast("You were caught by the Dean!", BAD);
        ToastSystem.addToast("You were stuck being lectured for " + Integer.toString(DEAN_TIME_PUNISHMENT) + "s!", BAD);
        negativeEventCounter++;
    }

    /**
     * This will end the sequence where the player is held in detention while the timer goes down.
     * This will unfreeze the player and dean.
     * It will rest the dean to the start of its patrol.
     */
    private void endPlayerCatch() {
        dean.setPosition(DEANSTARTPOS);
        dean.restartPath();
        dean.unfreeze();
        player.unfreeze();
        playerCaught = false;
    }

    /**
     * Use to drop remove the spikes in the chest room when the switch is pressed.
     */
    public static void dropSpikes() {
        if (!spikesLowered) {
            collisionSystem.removeCollisionByName("chestRoomSpikes");
            ToastSystem.addToast("You Lowered the Spikes!", GOOD);
            RenderingSystem.hideLayer("Spikes");
            RenderingSystem.hideLayer("Switch");
            spikesLowered = true;
        }
    }

    /**
     * <P>Teleports the player to a room on the tiled map based on the coordinates passed.</P>
     * <P>Each coordinate represents one room.</P>
     * <P>The width and height of each room in pixels is equivalent to the viewport width and height.</P>
     *
     * @param x the x coordinate of the room
     * @param y The y coordinate of the room
     * @param playerPos The coordinates of the player after they enter the room
     * @param deanPos Where the dean starts in this room
     * @param deanPath The path the dean takes in this room
     */
    public static void loadRoom(int x,int y, Vector2 playerPos, Vector2 deanPos, Character[] deanPath){
        collisionSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        TriggerSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        renderingSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        player.setX(playerPos.x);
        player.setY(playerPos.y);
        dean.setX(deanPos.x);
        dean.setY(deanPos.y);
        dean.setPath(deanPath);
    }

    /**
     * <P>Teleports the player to a room on the tiled map based on the coordinates passed.</P>
     * <P>Each coordinate represents one room.</P>
     * <P>The width and height of each room in pixels is equivalent to the viewport width and height.</P>
     *
     * @param x the x coordinate of the room
     * @param y The y coordinate of the room
     */
    public static void loadRoom(int x,int y){
        loadRoom(x,y,PLAYERSTARTPOS,DEANSTARTPOS,DEANPATH);
    }

    /**
     * <P>Teleports the player to a room on the tiled map based on the coordinates passed.</P>
     * <P>Each coordinate represents one room.</P>
     * <P>The width and height of each room in pixels is equivalent to the viewport width and height.</P>
     *
     * @param x the x coordinate of the room
     * @param y The y coordinate of the room
     * @param playerPos The coordinates of the player after they enter the room
     */
    public static void loadRoom(int x,int y, Vector2 playerPos){
        loadRoom(x,y,playerPos,DEANSTARTPOS,DEANPATH);
    }


    @Override
    public void resize(int width, int height) {
        renderingSystem.resize(width, height);
    }

    @Override
    public void pause() {
        togglePause();
    }

    @Override
    public void resume() {
        togglePause();
    }

    @Override
    public void dispose(){
        leaderBoard.saveToFile(Main.leaderBoardFilePath);
    }
}
