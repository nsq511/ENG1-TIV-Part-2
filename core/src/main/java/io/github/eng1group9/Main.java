package io.github.eng1group9;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.entities.*;
import io.github.eng1group9.systems.*;

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
    public static boolean prisonDoorOpened = false;
    public static boolean lockpickRoomOpened = false;
    public static boolean PNQDoorOpened  = false;
    public static boolean releasedPope = false;
    public static boolean bossConverted = false;
    public static boolean bobReleased  = false;
    public static boolean defeatedBoss = true;
    public static int longboiBonus = 0; // the bonus to add based on wether LongBoi was found.
    public static int hiddenEventCounter = 0;
    public static int negativeEventCounter = 0;
    public static int positiveEventCounter = 0;
    public static final String leaderBoardFilePath = "leaderboard.txt";

    public static LeaderBoard leaderBoard = LeaderBoard.loadFromFile(leaderBoardFilePath, 10);

    private static TimerSystem timerSystem = new TimerSystem(TIMERSTARTVALUE);
    public static boolean showCollision = false;

    private List<Rectangle> worldCollision;
    final static int LONGBOIBONUSAMOUNT = 50;
    final static String TMXPATH = "World/testMap.tmx";

    final static Vector2 prisonCoords = new Vector2(3,0);
    final static Vector2 playerPrisonPos = new Vector2(380,200);

    public static Player player;
    private static boolean playerCaught = false; // Whether the player is currently being held by the Dean.
    final static float INITALPLAYERCAUGHTTIME = 1.2f;
    private static float playerCaughtTime = INITALPLAYERCAUGHTTIME; // how many seconds the Dean will hold the player when caught.
    final static Vector2 PLAYERSTARTPOS = new Vector2(730, 500); // Where the player begins the game, and returns to when caught.
    final static float DEFAULTPLAYERSPEED = 200; // The players speed.

    private static Dean dean;
    final static Vector2 DEANSTARTPOS = new Vector2(32, 352); // Where the Dean begins the game, and returns to after catching the player.
    static final float DEFAULTDEANSPEED = 100;
    final int DEANPUNISHMENT = 30; // The number of seconds the Dean adds to the timer.
    final static Character[] DEFAULTDEANPATH = { // The path the dean will take in the first room (D = Down, U = Up, L = Left, R = Right). The path will loop.
        'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
        'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
        'R', 'R', 'R',
        'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U',
        'L', 'L', 'L',
        'D', 'D', 'D', 'D',
        'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L'
    };

    final static Color BAD = new Color(1,1,0,1);
    final static Color GOOD = new Color(0,1,1,1);

    public static Main instance;
    public static RenderingSystem renderingSystem = new RenderingSystem();
    public static CollisionSystem collisionSystem = new CollisionSystem();
    public static InputSystem inputSystem = new InputSystem();

    private static Boss boss;
    final static float BOSSSPEED = 0;
    final static Vector2 BOSSSTARTPOS = new Vector2(450, 255);
    private static float BOSSATTACKCOOLDOWN = 2.5f;
    final static float BOSSFIGHTLENGTH = 60f;

    private static ArrayList<BossProjectile> projectiles;
    private static ArrayList<ProjectileWarning> projectileWarnings;
    private static float PROJECTILESPEED = 1000f;
    private static final float PROJECTILEWARNINGLENGTH = 2.5f;
    private static final int PROJECTILESPACING = 96;

    @Override
    public void create() {
        renderingSystem.initWorld(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        collisionSystem.init(renderingSystem.getMapRenderer().getMap());
        TriggerSystem.init(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        RoomSystem.init(TMXPATH,  VIEWPORTWIDTH, VIEWPORTHEIGHT);
        worldCollision = collisionSystem.getWorldCollision();
        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEFAULTDEANPATH);
        boss = new Boss(BOSSSTARTPOS,BOSSSPEED, false,BOSSATTACKCOOLDOWN,PROJECTILESPACING);
        projectiles = new ArrayList<>();
        projectileWarnings = new ArrayList<>();
        loadRoom(0, 0, PLAYERSTARTPOS);
        togglePause();
        instance = this;
    }

    /**
     * Initialises variables such as game states and entity positions. Used on initialisation and reset
     */
    public static void initSystem(){
        gameState = 0; // Not started
        projectiles = new ArrayList<>();
        projectileWarnings = new ArrayList<>();
        chestDoorOpen = false;
        exitOpen = false;
        spikesLowered = false;
        scrollUsed = false;
        prisonDoorOpened = false;
        lockpickRoomOpened = false;
        PNQDoorOpened  = false;
        releasedPope = false;
        bossConverted = false;
        bobReleased  = false;
        defeatedBoss = true;
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
        boss.reset();
        RenderingSystem.reset();
        collisionSystem.reset();
        loadRoom(0, 0, PLAYERSTARTPOS);
    }

    @Override
    public void render() {
        inputSystem.handle(player);
        if (gameState == 1) logic();
        draw();
    }

    /**
     * Where the game processes its logic each frame.
     * It will not run if the game is paused.
     */
    public void logic() {
        timerSystem.tick();
        dean.nextMove();
        boss.logic();
        checkProjectiles();
        checkDeanCatch();
        TriggerSystem.checkTouchTriggers(player);
        player.update();
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
            longboiBonus = LONGBOIBONUSAMOUNT;
            ToastSystem.addToast("You found my potion! Thank you!", messageColour);
            RenderingSystem.showLayer("LONGBOI");
            hiddenEventCounter++;
        }
    }

    public void draw() {
        renderingSystem.draw(player, dean, boss, showCollision, timerSystem.elapsedTime, worldCollision, projectiles, projectileWarnings);
        switch (gameState) {
            case 0:
                renderingSystem.renderStartOverlay(960, 640);
                break;
            case 2:
                renderingSystem.renderPauseOverlay(960, 640, positiveEventCounter, negativeEventCounter, hiddenEventCounter);
                break;
            case 3:
                renderingSystem.renderWinOverlay(960, 640, timerSystem.getTimeLeft(), calculateScore(), positiveEventCounter, negativeEventCounter, hiddenEventCounter);
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

    public static void defeatBoss(){
        if(!defeatedBoss){
            defeatedBoss = true;
            collisionSystem.removeCollisionByName("BossBarrier");
            RenderingSystem.showLayer("Boss");
            RenderingSystem.hideLayer("Flames");
            RenderingSystem.hideLayer("LibraryBookshelves1");
            RenderingSystem.hideLayer("LibraryBookshelves2");
            RenderingSystem.hideLayer("LibraryBookshelves3");
            RenderingSystem.hideLayer("Pyre");
            RenderingSystem.showLayer("Staff");
            RenderingSystem.showLayer("ExitKey");
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
            initSystem();
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
     * Calculate the players score based on how much time was left and wether they found Long Boi.
     * @return The score.
     */
    public static int calculateScore() {
        return TimerSystem.getTimeLeft() + longboiBonus;
    }

    /**
     * Toggle wether the window should be in Windowed or Fullcreen mode.
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
     * Toggle wether the game should be paused.
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
     * Checks if the dean has caught the player, and punishes them if he has by removing 50s from time left.
     */
    public void checkDeanCatch() {
        if (dean.canReach(player) && !playerCaught) {
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
        loadRoom(prisonCoords, playerPrisonPos);
        player.freeze();
        timerSystem.addGradually(DEANPUNISHMENT - INITALPLAYERCAUGHTTIME);
        ToastSystem.addToast("You were thrown in the dungeon by the Dean!", BAD);
        ToastSystem.addToast("Luckily for you the door isn't very sturdy, it only takes you " + Integer.toString(DEANPUNISHMENT) + "s to break it down", BAD);
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

    public static void checkProjectiles(){
        for(int i = projectileWarnings.size() - 1; i >= 0; i--){
            ProjectileWarning warning = projectileWarnings.get(i);
            if(warning.getWarningLength() <= 0){
                projectileWarnings.remove(i);
            }
        }
        for(int i = projectiles.size() - 1; i >= 0; i--){
            BossProjectile projectile = projectiles.get(i);
            projectile.nextMove();
            if(projectile.hittingPlayer(player)){
                player.damage();
            }
            int removeOffset = 100; // how far off the screen in pixels the projectile is before it is removed

            if(projectile.getX() < 0-removeOffset || projectile.getX() > VIEWPORTWIDTH*2+removeOffset ||
                projectile.getY() < 0-removeOffset || projectile.getY() > VIEWPORTHEIGHT*2+removeOffset)
            {
                projectiles.remove(projectile);
            }
        }
    }

    public static void spawnProjectile(Vector2 position, Character direction){
        float warningLength = PROJECTILEWARNINGLENGTH;
        float delta = Gdx.graphics.getDeltaTime();
        ProjectileWarning warning = new  ProjectileWarning(position, direction, warningLength);
        BossProjectile projectile = new BossProjectile(position, direction, PROJECTILESPEED, PROJECTILEWARNINGLENGTH);
        projectileWarnings.add(warning);
        projectiles.add(projectile);
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

    public static void openLockpickRoomDoor() {
        if(!lockpickRoomOpened && (player.hasLockpick() || player.hasJanitorKey())){
            collisionSystem.removeCollisionByName("lockpickRoomDoor");
            RenderingSystem.hideLayer("LockpickRoomDoor");
            lockpickRoomOpened = true;
        }
    }

    public static void openPrisonDoor() {
        if(!prisonDoorOpened && (player.hasLockpick() || player.hasJanitorKey())){
            collisionSystem.removeCollisionByName("prisonDoor");
            RenderingSystem.hideLayer("PrisonDoor");
            prisonDoorOpened = true;
        }
    }

    public static void openPNQDoor(){
        if(!PNQDoorOpened && (player.hasLockpick() || player.hasJanitorKey())){
            collisionSystem.removeCollisionByName("PNQDoor");
            RenderingSystem.hideLayer("PNQDoor");
            PNQDoorOpened = true;
        }
    }

    public static void releasePope(){
        if(!releasedPope){
            releasedPope = true;
            collisionSystem.removeCollisionByName("popeCellDoor");
            RenderingSystem.hideLayer("Pope");
            RenderingSystem.hideLayer("PopeCellDoor");
            RenderingSystem.showLayer("PopeSeraph");
            ToastSystem.addToast("\"GSZMP BLF UIRVMW! R DROO KIZB ULI BLFI ERXGLIB!\"");
        }
    }

    public static void convertBoss(){
        bossConverted = true;
        winGame();
    }

    public static void releaseBob(){
        if(player.hasLockpick()){
            bobReleased = true;
            LoseGame();
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
     */
    public static void loadRoom(int x,int y, Vector2 playerPos){

        RoomSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        collisionSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        TriggerSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        renderingSystem.loadRoom(x,y,VIEWPORTWIDTH,VIEWPORTHEIGHT);
        player.setX(playerPos.x);
        player.setY(playerPos.y);

        dean.deactivate();
        if(x == 0 && y == 0){
            dean.activate();
        }
        else if(x == 1 && y == 0){
            dean.activate();
            Vector2 pos = new Vector2(80,64);
            Character[] path = {
                'R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R','R',
                'L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L','L'
            };

            dean = new Dean(pos, DEFAULTDEANSPEED, path);
        }
        else if(x == 2 && y == 1){
            dean.activate();
            Vector2 pos = new Vector2(250,400);
            Character[] path = {
                'D','D','D','D','D','D',
                'U','U','U','U','U','U',
                'R','R','R','R','R','R','R','R','R','R','R',
                'D','D','D','D','D','D',
                'U','U','U','U','U','U',
                'D','D','D','D','D','D',
                'L','L','L','L','L','L','L','L','L','L','L',
                'U','U','U','U','U','U'
            };

            dean = new Dean(pos, 2000, path);
        }
        else if(x == 1 && y == 2){
            dean.activate();
            Vector2 pos = new Vector2(16,490);
            Character[] path = {
                'R',
                'L'
            };

            dean = new Dean(pos, DEFAULTDEANSPEED, path);
        }
        else if(x == 2 && y == 2){
            if(!boss.isDefeated()){
                ToastSystem.addToast("You sense an evil presence at the end of this hallway...", BAD);
            }
        }
        else if (x == 3 && y == 2) {
            if(!boss.isDefeated()){
                RenderingSystem.showLayer("Flames");
                boss.start(BOSSFIGHTLENGTH);
                RenderingSystem.hideLayer("Boss");
            }
        }
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
        loadRoom(x,y,PLAYERSTARTPOS);
    }

    /**
     * <P>Teleports the player to a room on the tiled map based on the coordinates passed.</P>
     * <P>Each coordinate represents one room.</P>
     * <P>The width and height of each room in pixels is equivalent to the viewport width and height.</P>
     *
     * @param coordinates the coordinates of the room
     */
    public static void loadRoom(Vector2 coordinates, Vector2 playerPos)
    {
        int x = (int)coordinates.x;
        int y = (int)coordinates.y;
        loadRoom(x,y,playerPos);
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
