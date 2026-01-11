package io.github.eng1group9;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.entities.*;
import io.github.eng1group9.systems.*;

import javax.swing.text.DefaultEditorKit;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
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
    public static boolean outsideDoorOpen = false; // Whether the door to the outside room has been opened.
    public static boolean exitOpen = false; // Whether the exit is open/
    public static boolean spikesLowered = false; // Whether the spikes in the chest room have been lowered.
    public static boolean cloakUsed = false; // Whether the cloak power up has been collected.
    public static boolean dungeonDoorOpened = false;
    public static boolean lockpickRoomOpened = false;
    public static boolean PNQDoorOpened = false;
    public static boolean releasedPope = false;
    public static boolean bossConverted = false;
    public static boolean bobReleased = false;
    public static boolean defeatedBoss = false;
    public static int longboiBonus = 25; // the bonus to add based on whether LongBoi was found.
    public static boolean bookUsed = false; // Whether the book power up has been collected.
    public static boolean potionAch = false;
    public static String[] potions = { "speed", "slow", "dark" };
    public static int hiddenEventCounter = 0;
    public static int negativeEventCounter = 0;
    public static int positiveEventCounter = 0;
    public static int speedCounter = 0;
    public static int slowCounter = 0;
    public static int darknessCounter = 0;
    public static final String leaderBoardFilePath = "leaderboard.txt";

    public static LeaderBoard leaderBoard = LeaderBoard.loadFromFile(leaderBoardFilePath, 5);

    private static TimerSystem timerSystem = new TimerSystem(TIMERSTARTVALUE);
    public static boolean showCollision = false;

    private List<Rectangle> worldCollision;
    // private List<Rectangle> doors;
    final static String TMXPATH = "World/testMap.tmx";

    final static Vector2 PRISONCOORDS = new Vector2(3, 0);
    final static Vector2 PLAYERPRISONPOS = new Vector2(380, 200);

    final static float DOORCOOLDOWN = 0.5f;
    private static int currentRoomX;
    private static int currentRoomY;

    public static Player player;
    private static boolean playerCaught = false; // Whether the player is currently being held by the Dean.
    private static boolean playerCaughtByLibrarian = false;
    final static float INITALPLAYERCAUGHTTIME = 1.2f;
    private static float playerCaughtTime = INITALPLAYERCAUGHTTIME; // how many seconds the Dean will hold the player when caught.
    final static Vector2 PLAYERSTARTPOS = new Vector2(150, 500); // Where the player begins the game, and returns to when caught.
    final static float DEFAULTPLAYERSPEED = 200; // The players speed.

    private static Dean dean;
    final static Vector2 DEANSTARTPOS = new Vector2(32, 352); // Where the Dean begins the game, and returns to after
                                                              // catching the player.
    static final float DEFAULTDEANSPEED = 100;
    final int DEANPUNISHMENT = 30; // The number of seconds the Dean adds to the timer.
    final static Character[] DEFAULTDEANPATH = { // The path the dean will take in the first room (D = Down, U = Up, L =
                                                 // Left, R = Right). The path will loop.
            'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
            'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
            'R', 'R', 'R',
            'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U',
            'L', 'L', 'L',
            'D', 'D', 'D', 'D',
            'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L'
    };

    private static Dean librarian;
    final static Vector2 LIBRARIANSTARTPOS = new Vector2(224, 15);
    final static float DEFAULTLIBRARIANSPEED = 150;
    final static Character[] LIBRARIANPATH = { 'R', 'R', 'R', 'R', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U',
            'U', 'U', 'U', 'U', 'U', 'U', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
            'D', 'D', 'L', 'L', 'L', 'L' };

    final static Color BAD = new Color(1f, 1f, 0f, 1f);
    final static Color GOOD = new Color(0f, 1f, 1f, 1f);
    final static Color ACHIEVEMENT = new Color(1f, 0.7f, 0.1f, 1f);

    public static Main instance;
    public static RenderingSystem renderingSystem = new RenderingSystem();
    public static CollisionSystem collisionSystem = new CollisionSystem();
    public static InputSystem inputSystem = new InputSystem();

    private static Boss boss;
    final static Vector2 BOSSSTARTPOS = new Vector2(450, 255);
    private static float BOSSATTACKCOOLDOWN = 2.5f;
    final static float BOSSFIGHTLENGTH = 45f;

    private static ArrayList<BossProjectile> projectiles;
    private static ArrayList<ProjectileWarning> projectileWarnings;
    private static float PROJECTILESPEED = 500f;
    private static final float PROJECTILEWARNINGLENGTH = 2.5f;
    private static final int PROJECTILESPACING = 96;
    // Achievement names
    public static final String ACH_LONGBOI = "Bird Spotter";
    public final static int LONGBOIBONUSAMOUNT = 50;
    public static final String ACH_DEAN_CAPTURES = "Troublemaker";
    public static final int ACH_DEAN_CAPTURE_POINT_PUNISHMENT = -20; // The points lost for getting the dean capture
                                                                     // achievement
    public static final String ACH_INVIS = "Now you see me...";
    public static final String ACH_LOST = "Detour Champion";
    public static boolean achTriggered = false;
    public static final String ACH_QUICK = "Fancy a Quickie?";
    public static final String ACH_BOOK = "Thief";
    public static final String ACH_POTION = "A true Witch";
    public static final String ACH_ARSONIST = "Arsonist";
    public static final String ACH_WORLD_ENDER = "World ender";
    public static final String ACH_SELF_SERVING = "Self-serving";
    public static final String ACH_PROSELYTISER = "Proselytiser";
    public static final String ACH_COWARD = "COWARD!";
    public static final String ACH_TRUE_AND_PERFECT_KNIGHT = "A true and perfect knight";
    public static final String ACH_ERUDITE = "Erudite";
    public static final String ACH_PAPAL_AUTHORITY = "Papal authority";


    @Override
    public void create() {
        renderingSystem.initWorld(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        collisionSystem.init(renderingSystem.getMapRenderer().getMap());
        TriggerSystem.init(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        RoomSystem.init(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT, DOORCOOLDOWN);
        worldCollision = collisionSystem.getWorldCollision();
        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEFAULTDEANPATH);
        boss = new Boss(BOSSSTARTPOS,BOSSATTACKCOOLDOWN,PROJECTILESPACING);
        librarian = new Dean(LIBRARIANSTARTPOS, DEFAULTLIBRARIANSPEED, LIBRARIANPATH,
                "Characters/librarianAnimations.png");
        projectiles = new ArrayList<>();
        projectileWarnings = new ArrayList<>();
        loadRoom(0, 0, PLAYERSTARTPOS, LIBRARIANSTARTPOS, LIBRARIANPATH);
        togglePause();
        instance = this;

        AchievementSystem.init();
    }

    /**
     * Initialises variables such as game states and entity positions. Used on
     * initialisation and reset
     */
    public static void initSystem() {
        gameState = 0; // Not started
        boss.deactivate();
        projectiles = new ArrayList<>();
        projectileWarnings = new ArrayList<>();
        chestDoorOpen = false;
        outsideDoorOpen = false;
        exitOpen = false;
        spikesLowered = false;
        cloakUsed = false;
        dungeonDoorOpened = false;
        lockpickRoomOpened = false;
        PNQDoorOpened = false;
        releasedPope = false;
        bossConverted = false;
        bobReleased = false;
        defeatedBoss = false;
        bookUsed = false;
        longboiBonus = 0;
        hiddenEventCounter = 0;
        negativeEventCounter = 0;
        positiveEventCounter = 0;
        speedCounter = 0;
        slowCounter = 0;
        darknessCounter = 0;
        potionAch = false;
        timerSystem.reset();
        playerCaught = false;
        playerCaughtTime = INITALPLAYERCAUGHTTIME;
        player.reset();
        dean.reset();
        boss.reset();
        librarian.reset();
        librarian.freeze();
        RenderingSystem.reset();
        collisionSystem.reset();
        RenderingSystem.hideLayer("PopeSeraph");
        RenderingSystem.hideLayer("Staff");
        RenderingSystem.hideLayer("Boss");
        loadRoom(0, 0, PLAYERSTARTPOS, LIBRARIANSTARTPOS, LIBRARIANPATH);
        TriggerSystem.init(TMXPATH, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        AchievementSystem.reset();
        achTriggered = false;
    }

    @Override
    public void render() {
        inputSystem.handle(player);
        if (gameState == 1)
            logic();
        draw();
    }

    /**
     * Check if the player has the red potion.
     * If they do, show Long Boi and complete the hidden event.
     * If not, tell the player they must find the potion.
     */
    public static void checkForLongboi() {
        Color messageColour = new Color(0.2f, 1, 0.2f, 1);
        if (longboiBonus == 0 && !player.hasRedPotion()) {
            ToastSystem.addToast("Hello There! I seem to have misplaced my Red Potion, could you get it for me?",
                    messageColour);
        } else if (longboiBonus == 0 && player.hasRedPotion()) {
            ToastSystem.addToast("You found my potion! Thank you!", messageColour);
            RenderingSystem.showLayer("LONGBOI");
            hiddenEventCounter++;
            incAchievement(ACH_LONGBOI); // Trigger longboi achievement
        }
    }

    /**
     * Adds an achievement toast
     *
     * @param text The
     */
    private static void achNotif(String achievementName) {
        ToastSystem.addToast("Achievement: " + achievementName, ACHIEVEMENT);
    }

    /**
     * Increments an achievement and adds a toast message if this increment acquired
     * the achievement
     *
     * @param achievement_name The name of the achievment to increment
     *
     * @return Whether the achievement was acquired in this increment
     */
    private static boolean incAchievement(String achievement_name) {
        boolean ach_achieved = AchievementSystem.incAchievement(achievement_name);
        if (ach_achieved) {
            achNotif(achievement_name);
        }
        return ach_achieved;
    }

    public void draw() {
        renderingSystem.draw(player, dean, boss, librarian, showCollision, TimerSystem.elapsedTime, worldCollision,
                projectiles, projectileWarnings);
        switch (gameState) {
            case 0:
                renderingSystem.renderStartOverlay(960, 640);
                break;
            case 2:
                renderingSystem.renderPauseOverlay(960, 640, positiveEventCounter, negativeEventCounter,
                        hiddenEventCounter);
                break;
            case 3:
                renderingSystem.renderWinOverlay(960, 640, TimerSystem.getTimeLeft(), calculateScore(),
                        positiveEventCounter, negativeEventCounter, hiddenEventCounter);
                break;
            case 4:
                renderingSystem.renderLoseOverlay(960, 640, positiveEventCounter, negativeEventCounter,
                        hiddenEventCounter);
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
        if(!chestDoorOpen){
            if (player.hasChestRoomKey() || player.hasLockpick()) {
                ToastSystem.addToast("You Opened the Door!", GOOD);
                collisionSystem.removeCollisionByName("chestRoomDoor");
                RenderingSystem.hideLayer("ChestDoorClosed");
                chestDoorOpen = true;
            }
            else{
                ToastSystem.addToast("The door is locked!", BAD);
            }
        }
    }

    public static void defeatBoss(){
        if(!defeatedBoss){
            System.out.println("Boss defeated!");
            defeatedBoss = true;
            collisionSystem.removeCollisionByName("BossBarrier");
            RoomSystem.unlockDoor(20);
            RenderingSystem.showLayer("Boss");
            RenderingSystem.hideLayer("Flames");
            RenderingSystem.hideLayer("LibraryBookshelves1");
            RenderingSystem.hideLayer("LibraryBookshelves2");
            RenderingSystem.hideLayer("LibraryBookshelves3");
            RenderingSystem.hideLayer("Pyre");
            RenderingSystem.showLayer("Staff");
            RenderingSystem.showLayer("ExitKey");
            incAchievement(ACH_ARSONIST);
        }
    }

    public static void openOutsideRoomDoor() {
        if (!outsideDoorOpen) {
            ToastSystem.addToast("You Opened the Door!", GOOD);
            collisionSystem.removeCollisionByName("outsideRoomDoor");
            RenderingSystem.hideLayer("OutsideDoorClosed");
            outsideDoorOpen = true;
        }
    }

    // END OF todo

    /**
     * Open the exit.
     * Remove its hitbox and hide its graphic.
     */
    public static void openExit() {
        if (player.hasExitKey() && !exitOpen) {
            ToastSystem.addToast("You Opened the Exit!", GOOD);
            collisionSystem.removeCollisionByName("exitDoor");
            exitOpen = true;
        }
    }

    /**
     * Checks if the player has read 7 books, which gives the erudite achievement
     */
    public static void checkErudite(){
        if(player.booksRead() >= 7){
            incAchievement(ACH_ERUDITE);
        }
    }

    /**
     * Give the scroll powerup to the player, making the player invisible for 15s.
     * Hide the scroll graphic.
     */
    public static void getcloak() {
        if (!cloakUsed) {
            incAchievement(ACH_INVIS); // Trigger invisible scroll achievement
            player.becomeInvisible();
            cloakUsed = true;
            ToastSystem.addToast("You found the invisibility cloak!", GOOD);
            positiveEventCounter++;
        }
    }

    public static void getBook() {
        if (!bookUsed) {
            librarian.unfreeze();
            RenderingSystem.hideLayer("Book");
            bookUsed = true;
            ToastSystem.addToast("You stole a book...", BAD);
            ToastSystem.addToast("RUN!!!!", BAD);
            negativeEventCounter++;
            incAchievement(ACH_BOOK);

        }
    }

    public static void getPotion() {
        String effect = potions[MathUtils.random(potions.length - 1)];

        if (effect == "speed") {
            player.speedPotion();
            ToastSystem.addToast("SPEED POTION ACTIVATED", GOOD);
            ToastSystem.addToast("Press Q to Dash", GOOD);
            positiveEventCounter++;
            speedCounter++;

        }
        if (effect == "slow") {
            player.slownessPotion();
            ToastSystem.addToast("SLOW POTION ACTIVATED", BAD);
            negativeEventCounter++;
            slowCounter++;

        }
        if (effect == "dark") {
            renderingSystem.activateDarkness();
            ToastSystem.addToast("DARKNESS POTION ACTIVATED", BAD);
            ToastSystem.addToast("Who turned off the lights...?", BAD);
            negativeEventCounter++;
            darknessCounter++;

        }

        if (!potionAch && speedCounter >= 1 && slowCounter >= 1 && darknessCounter >= 1) {
            incAchievement(ACH_POTION);
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
        } else if (gameState == 3 || gameState == 4) {
            initSystem();
            gameState = 1;
        }
    }

    /**
     * Set the game to the win state, used when the player escapes.
     */
    public static void winGame() {
        togglePause();

        if(!bobReleased){
            if (TimerSystem.elapsedTime <= 60) {
                incAchievement(ACH_QUICK); // Trigger quick finish achievement
            }

            if(player.hasStaff() && player.booksRead() >= 7 && !releasedPope && !bossConverted){
                incAchievement(ACH_SELF_SERVING);
            }

            if(bossConverted){
                incAchievement(ACH_PROSELYTISER);
            }

            if(releasedPope && !bossConverted && defeatedBoss){
                incAchievement(ACH_TRUE_AND_PERFECT_KNIGHT);
            }

            if(!releasedPope && !defeatedBoss){
                incAchievement(ACH_COWARD);
            }
        }
        else{
            incAchievement(ACH_WORLD_ENDER);
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
     * Calculate the players score based on how much time was left and wether they
     * found Long Boi.
     *
     * @return The score.
     */
    public static int calculateScore() {
        int score = TimerSystem.getTimeLeft();
        score = (int) AchievementSystem.modifyScore(score);
        return score;
    }

    /**
     * Toggle whether the window should be in Windowed or Fullcreen mode.
     */
    public void toggleFullscreen() {
        if (isFullscreen) {
            Gdx.graphics.setWindowedMode(960, 640);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        isFullscreen = !isFullscreen;
    }

    /**
     * Toggle wether the game should be paused.
     * This will freeze the player/dean, stop all game logic and display the pause
     * overlay.
     */
    public static void togglePause() {
        if (gameState == 2) {
            if (!playerCaught) {
                player.unfreeze();
                dean.unfreeze();
                if (bookUsed) {
                    librarian.unfreeze();
                }
            }
            gameState = 1;
        } else {
            player.freeze();
            dean.freeze();
            librarian.freeze();
            if (gameState == 1)
                gameState = 2;
        }
    }

    /**
     * Where the game processes its logic each frame.
     * It will not run if the game is paused.
     */
    public void logic() {
        timerSystem.tick();

        if (!achTriggered && TimerSystem.elapsedTime > 4 * 60) {
            achTriggered = incAchievement(ACH_LOST); // Trigger slow finish achievement
        }

        dean.nextMove();
        checkDeanCatch();

        librarian.nextMove();

        boss.logic();
        checkProjectiles();

        TriggerSystem.checkTouchTriggers(player);
        RoomSystem.checkCooldown();
        player.update();

    }

    /**
     * Checks if the dean has caught the player, and punishes them if he has by
     * removing 50s from time left.
     */
    public void checkDeanCatch() {
        if (dean.canReach(player) && !playerCaught) {
            incAchievement(ACH_DEAN_CAPTURES); // Increment count for Dean capture achievment
            startPlayerCatch();
        } else if (librarian.canReach(player) && !playerCaught) {
            incAchievement(ACH_DEAN_CAPTURES); // Increment count for Dean capture achievment
            playerCaughtByLibrarian = true;
            startPlayerCatch();
        } else if (playerCaught) {
            if (playerCaughtTime <= 0) {
                endPlayerCatch();
                playerCaughtTime = INITALPLAYERCAUGHTTIME;
            } else {
                float delta = Gdx.graphics.getDeltaTime();
                playerCaughtTime -= delta;
            }
        }
    }

    /**
     * Run when the player is first caught by the Dean.
     * This will begin the sequence where the player is held in detention while the
     * timer goes down.
     * This will freeze the player and dean.
     */
    private void startPlayerCatch() {
        playerCaught = true;
        loadRoom(PRISONCOORDS, PLAYERPRISONPOS);
        player.freeze();

        if (playerCaughtByLibrarian) {
            librarian.freeze();
            librarian.changeAnimation(3);
            librarian.setPosition(PLAYERSTARTPOS.x + 32, PLAYERSTARTPOS.y);
            timerSystem.addGradually(DEANPUNISHMENT - INITALPLAYERCAUGHTTIME);
            ToastSystem.addToast("Librarian took his book back", BAD);
            ToastSystem.addToast(
                    "You were stuck being his assistant for " + Integer.toString(DEANPUNISHMENT) + "s!", BAD);
        } else {
            // THE PREVIOUS DEAN CAPTURE BEHAVIOUR
            /**
             * dean.freeze();
             * dean.changeAnimation(3);
             * dean.setPosition(PLAYERSTARTPOS.x + 32, PLAYERSTARTPOS.y);
             * timerSystem.addGradually(DEANPUNISHMENT - INITALPLAYERCAUGHTTIME);
             * ToastSystem.addToast("You were caught by the Dean!", BAD);
             * ToastSystem.addToast("You were stuck being lectured for " +
             * Integer.toString(DEANPUNISHMENT) + "s!",
             * BAD);
             * ToastSystem.addToast("Luckily for you the door isn't very sturdy, it only
             * takes you " + Integer.toString(DEANPUNISHMENT) + "s to break it down", BAD);
             */

            timerSystem.addGradually(DEANPUNISHMENT - INITALPLAYERCAUGHTTIME);
            ToastSystem.addToast("You were thrown in the dungeon by the Dean!", BAD);
            ToastSystem.addToast("Luckily for you the door isn't very sturdy, it only takes you "
                    + Integer.toString(DEANPUNISHMENT) + "s to break it down", BAD);
            player.setPosition(PLAYERPRISONPOS);
        }
        negativeEventCounter++;
    }

    /**
     * This will end the sequence where the player is held in detention while the
     * timer goes down.
     * This will unfreeze the player and dean.
     * It will rest the dean to the start of its patrol.
     */
    private void endPlayerCatch() {
        if (playerCaughtByLibrarian) {
            librarian.setPosition(LIBRARIANSTARTPOS);
            librarian.restartPath();
            librarian.unfreeze();
        } else {
            dean.setPosition(DEANSTARTPOS);
            dean.restartPath();
            dean.unfreeze();
        }
        player.unfreeze();
        playerCaught = false;
        playerCaughtByLibrarian = false;
    }

    public static void checkProjectiles() {
        for (int i = projectileWarnings.size() - 1; i >= 0; i--) {
            ProjectileWarning warning = projectileWarnings.get(i);
            if (warning.getWarningLength() <= 0) {
                projectileWarnings.remove(i);
            }
        }
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            BossProjectile projectile = projectiles.get(i);
            projectile.nextMove();
            if (projectile.hittingPlayer(player)) {
                player.damage();
            }
            int removeOffset = 100; // how far off the screen in pixels the projectile is before it is removed

            if (projectile.getX() < 0 - removeOffset || projectile.getX() > VIEWPORTWIDTH * 2 + removeOffset ||
                    projectile.getY() < 0 - removeOffset || projectile.getY() > VIEWPORTHEIGHT * 2 + removeOffset) {
                projectiles.remove(projectile);
            }
        }
    }

    /**
     * Spawns a projectile, should only be used for the boss fight.
     * @param position - the position of the projectile
     * @param direction - the direction that the projectile is travelling
     */
    public static void spawnProjectile(Vector2 position, Character direction){
        float warningLength = PROJECTILEWARNINGLENGTH;
        //float delta = Gdx.graphics.getDeltaTime();
        ProjectileWarning warning = new ProjectileWarning(position, direction, warningLength);
        BossProjectile projectile = new BossProjectile(position, direction, PROJECTILESPEED, PROJECTILEWARNINGLENGTH);
        projectileWarnings.add(warning);
        projectiles.add(projectile);
        System.out.println(projectiles.size());
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
     * Used to open the door to the room with the chest containing the lockpick
     */
    public static void openLockpickRoomDoor() {
        if(!lockpickRoomOpened){
            if (player.hasLockpick() || player.hasJanitorKey()) {
                collisionSystem.removeCollisionByName("lockpickRoomDoor");
                RenderingSystem.hideLayer("LockpickRoomDoor");
                lockpickRoomOpened = true;
            }
            else{
                ToastSystem.addToast("The door is locked!", BAD);
            }
        }
    }

    /**
     * Used to open the door to the dungeons from the outside
     */
    public static void openDungeonDoor() {
        if(!dungeonDoorOpened){
            if (player.hasLockpick() || player.hasJanitorKey()) {
                collisionSystem.removeCollisionByName("dungeonDoor");
                RenderingSystem.hideLayer("DungeonDoor");
                dungeonDoorOpened = true;
            }
            else{
                ToastSystem.addToast("The door is locked!", BAD);
            }
        }

    }

    /**
     * Used to open the locked door inside the PNQ building
     */
    public static void openPNQDoor() {
        if(!PNQDoorOpened){
            if (player.hasLockpick() || player.hasJanitorKey()) {
                collisionSystem.removeCollisionByName("PNQDoor");
                RenderingSystem.hideLayer("PNQDoor");
                PNQDoorOpened = true;
            }
            else{
                ToastSystem.addToast("The door is locked!", BAD);
            }
        }
    }

    /**
     * Used to release the pope from his cell, which reduces the length of the boss
     * fight.
     */
    public static void releasePope(){
        if(!releasedPope){
            if(player.hasLockpick()){
                releasedPope = true;
                collisionSystem.removeCollisionByName("popeCellDoor");
                RenderingSystem.hideLayer("Pope");
                RenderingSystem.hideLayer("PopeCellDoor");
                RenderingSystem.showLayer("PopeSeraph");
                incAchievement(ACH_PAPAL_AUTHORITY);
                ToastSystem.addToast("\"GSZMP BLF UIRVMW! R DROO KIZB ULI BLFI ERXGLIB!\"");
            }
            else{
                ToastSystem.addToast("\"I CANNOT STAND THIS IMPRISONMENT ANY LONGER! FIND A LOCKPICK AND RELEASE ME\"");
            }
        }
    }

    /**
     * Used to end the game by turning the boss into a pacifist
     */
    public static void convertBoss() {
        if (player.hasStaff()) {
            if (player.booksRead() >= 7) {
                bossConverted = true;
                winGame();
            } else {
                ToastSystem.addToast("If you knew how to use the staff, you could use it on him.", BAD);
            }
        }
    }

    /**
     * Used to release Bob from his cell, which ends the game.
     */
    public static void releaseBob() {
        if (player.hasLockpick()) {
            bobReleased = true;
            winGame();
        }
        else{
            ToastSystem.addToast("Luckily for you, the door is locked.");
        }
    }

    /**
     * <P>
     * Teleports the player to a room on the tiled map based on the coordinates
     * passed.
     * </P>
     * <P>
     * Each coordinate represents one room.
     * </P>
     * <P>
     * The width and height of each room in pixels is equivalent to the viewport
     * width and height.
     * </P>
     * <P>
     * Teleports the player to a room on the tiled map based on the coordinates
     * passed.
     * </P>
     * <P>
     * Each coordinate represents one room.
     * </P>
     * <P>
     * The width and height of each room in pixels is equivalent to the viewport
     * width and height.
     * </P>
     *
     * @param x         the x coordinate of the room
     * @param y         The y coordinate of the room
     * @param playerPos The coordinates of the player after they enter the room
     */
    public static void loadRoom(int x, int y, Vector2 playerPos, Vector2 librarianPos, Character[] librarianPath) {

        int offsetX = (x - currentRoomX) * VIEWPORTWIDTH * 2;
        int offsetY = (y - currentRoomY) * VIEWPORTHEIGHT * 2;

        RoomSystem.loadRoom(offsetX, offsetY);
        collisionSystem.loadRoom(offsetX, offsetY);
        TriggerSystem.loadRoom(offsetX, offsetY);
        renderingSystem.loadRoom(offsetX / 2, offsetY / 2);
        player.setX(playerPos.x);
        player.setY(playerPos.y);

        dean.deactivate();
        librarian.deactivate();

        // Spawn Room
        if (x == 0 && y == 0) {

            dean.activate();

            dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEFAULTDEANPATH);

            librarian.setX(librarianPos.x);
            librarian.setY(librarianPos.y);
            librarian.setPath(librarianPath);
            librarian.activate();
            if (!bookUsed) {
                librarian.freeze();
            }

        }

        // Coridoor Patrolling Room
        else if (x == 1 && y == 0) {
            dean.activate();
            Vector2 pos = new Vector2(80, 64);
            Character[] path = {
                    'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
                    'R', 'R', 'R', 'R',
                    'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L',
                    'L', 'L', 'L', 'L'
            };

            dean = new Dean(pos, DEFAULTDEANSPEED, path);
        }

        // Speed Buff Room
        else if (x == 2 && y == 1) {
            dean.activate();
            Vector2 pos = new Vector2(250, 400);
            Character[] path = {
                    'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
                    'D', 'D', 'D', 'D', 'D', 'D',
                    'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L',
                    'U', 'U', 'U', 'U', 'U', 'U'
            };

            dean = new Dean(pos, 2000, path);
        }

        // Guard Room
        else if (x == 1 && y == 2) {
            dean.activate();
            Vector2 pos = new Vector2(16, 490);
            Character[] path = {
                    'R',
                    'L'
            };

            dean = new Dean(pos, DEFAULTDEANSPEED, path);
        }

        // Bridge Room
        else if (x == 2 && y == 2) {
            if (!boss.isDefeated()) {
                ToastSystem.addToast("You sense an evil presence at the end of this hallway...", BAD);
            }
        }

        // Boss Room
        else if (x == 3 && y == 2) {
            if (!boss.isDefeated()) {
                RenderingSystem.showLayer("Flames");
                float timeModifier = 0f;
                if (releasedPope) {
                    timeModifier += 25f;
                }
                if (player.hasFirestarter()) {
                    timeModifier += 15f;
                }
                if(!defeatedBoss){
                    boss.start(BOSSFIGHTLENGTH-timeModifier);
                }
                RenderingSystem.hideLayer("Boss");
                RenderingSystem.hideLayer("Staff");
            }
        }
        currentRoomX = x;
        currentRoomY = y;
    }

    /**
     * <P>
     * Teleports the player to a room on the tiled map based on the coordinates
     * passed.
     * </P>
     * <P>
     * Each coordinate represents one room.
     * </P>
     * <P>
     * The width and height of each room in pixels is equivalent to the viewport
     * width and height.
     * </P>
     *
     * @param x the x coordinate of the room
     * @param y The y coordinate of the room
     */
    public static void loadRoom(Vector2 coordinates) {
        // loadRoom(coordinates, PLAYERSTARTPOS, LIBRARIANSTARTPOS, LIBRARIANPATH);
        loadRoom(coordinates, PLAYERSTARTPOS);
    }

    /**
     * <P>
     * Teleports the player to a room on the tiled map based on the coordinates
     * passed.
     * </P>
     * <P>
     * Each coordinate represents one room.
     * </P>
     * <P>
     * The width and height of each room in pixels is equivalent to the viewport
     * width and height.
     * </P>
     *
     * @param coordinates the coordinates of the room
     */
    public static void loadRoom(Vector2 coordinates, Vector2 playerPos) {
        int x = (int) coordinates.x;
        int y = (int) coordinates.y;
        loadRoom(x, y, playerPos, LIBRARIANSTARTPOS, LIBRARIANPATH);
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
    public void dispose() {
        leaderBoard.saveToFile(Main.leaderBoardFilePath);
    }
}
