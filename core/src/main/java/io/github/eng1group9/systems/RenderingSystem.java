package io.github.eng1group9.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.eng1group9.Main;
import io.github.eng1group9.entities.Dean;
import io.github.eng1group9.entities.Player;
import io.github.eng1group9.systems.ToastSystem.Toast;
import io.github.eng1group9.systems.TriggerSystem.Trigger;

import java.util.List;

/**
 * Handles drawing and displaying frames.
 */
public class RenderingSystem {
    private Texture missingTexture;
    private SpriteBatch worldBatch;
    private SpriteBatch uiBatch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthogonalTiledMapRenderer mapRenderer;
    private static TiledMap map;
    private Stage stage;
    private static TextField textfield;
    private static boolean firstLeaderBoard = true;

    private boolean darknessActive = false;
    private float darknessLeft = 0;
    private ShaderProgram vignette;
    private Texture whitePixel;

    /**
     * Takes and tileset and sets up a renderer to display it.
     * 
     * @param tmxPath        - The path to the tileset (.tmx file).
     * @param viewportWidth  - how many pixels wide the world is.
     * @param viewportHeight - how many pixels high the world is.
     */
    public void initWorld(String tmxPath, int viewportWidth, int viewportHeight) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, viewportWidth, viewportHeight);
        this.camera.update();
        this.viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        map = new TmxMapLoader().load(tmxPath);
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.missingTexture = new Texture("missingTexture.png");
        this.worldBatch = new SpriteBatch();
        this.uiBatch = new SpriteBatch();
        this.font = new BitmapFont();
        this.whitePixel = new Texture("whitePixel.png");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        textfield = new TextField("", skin);
        textfield.setPosition(960 * 0.1f, 640 / 2f + 20); // Change to not be hardcoded
        textfield.setSize(300, 30);
        stage.addActor(textfield);

        ShaderProgram.pedantic = false;
        vignette = new ShaderProgram(
                Gdx.files.internal("Shaders/vignette.vert"),
                Gdx.files.internal("Shaders/vignette.frag"));

    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    /**
     * Hide a layer so that tiles on it are NOT rendered.
     * 
     * @param name - The name of the layer.
     */
    public static void hideLayer(String name) {
        map.getLayers().get(name).setVisible(false);
    }

    /**
     * Resets layers
     */
    public static void reset() {
        textfield.setVisible(true);
        textfield.setText("");
        textfield.setDisabled(false);
        firstLeaderBoard = true;

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equals("LONGBOI")) {
                layer.setVisible(false);
            } else {
                layer.setVisible(true);
            }
        }
    }

    /**
     * Show a layer so that tiles on it are rendered.
     * 
     * @param name - The name of the layer.
     */
    public static void showLayer(String name) {
        map.getLayers().get(name).setVisible(true);
    }

    /**
     * Draw a frame to display.
     * 
     * @param player         - The current player object.
     * @param dean           - The dean object.
     * @param showCollision  - Wether to render the zones for collision / triggers
     *                       (dev mode).
     * @param elapsedTime    - How much time has passed since the game began.
     * @param worldCollision - A list of rectangles representing the games collison.
     */
    public void draw(Player player, Dean dean, Dean librarian, boolean showCollision, float elapsedTime,
            List<Rectangle> worldCollision) {
        update();
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        camera.update();
        mapRenderer.setView(camera);
        int[] belowPlayer = { 0, 1, 2, 3, 4, 5, 6 }; // the layers which should appear below the player
        mapRenderer.render(belowPlayer);

        worldBatch.begin();
        player.draw(worldBatch);
        dean.draw(worldBatch);
        librarian.draw(worldBatch);
        worldBatch.end();

        int[] abovePlayer = { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }; // the layers which should appear above
                                                                                 // the
        // player
        mapRenderer.render(abovePlayer);
        if (darknessActive) {

            // player centre in WORLD coords (use hitbox if you have one)
            Rectangle hb = player.getHitbox();
            float px = hb.x + hb.width * 0.5f;
            float py = hb.y + hb.height * 0.5f;

            worldBatch.setShader(vignette);
            worldBatch.begin();

            vignette.setUniformf("u_playerPos", px, py);
            vignette.setUniformf("u_radius", 60f); // world units (tile-based)
            vignette.setUniformf("u_softness", 60f);
            vignette.setUniformf("u_darkness", 0.98f);

            // draw a LARGE quad that covers the camera view
            worldBatch.draw(
                    whitePixel,
                    camera.position.x - camera.viewportWidth,
                    camera.position.y - camera.viewportHeight,
                    camera.viewportWidth * 3,
                    camera.viewportHeight * 3);

            worldBatch.end();
            worldBatch.setShader(null);
        }

        uiBatch.begin();
        font.draw(uiBatch, TimerSystem.getClockDisplay(), 10, 640 - 10);

        renderToasts(font, uiBatch);

        if (showCollision && worldCollision != null) { // show collisions for debugging
            renderCollision(uiBatch, worldCollision, player, dean, librarian);
            renderTriggers(uiBatch);
        }
        uiBatch.end();
    }

    /**
     * Render the toast display on the top left of the screen.
     * This is used to display text messages to the user for 5s.
     * 
     * @param font    The BitmapFont which used to render the text.
     * @param uiBatch - The SpriteBatch used for this (should be the ui batch).
     */
    public void renderToasts(BitmapFont font, SpriteBatch uiBatch) {
        ToastSystem.clearExpiredToasts();
        List<Toast> toasts = ToastSystem.getToasts();
        int offset = 0;

        for (Toast t : toasts) {
            offset += 30;
            font.setColor(t.getColour());
            font.draw(uiBatch, t.getText(), 10, (640 - 10) - offset);
        }
        font.setColor(1, 1, 1, 1);
    }

    /**
     * Render the zones for collision / triggers (dev mode).
     * 
     * @param uiBatch        - The SpriteBatch used for this (should be the ui
     *                       batch).
     * @param worldCollision - A list of rectangles representing the games collison.
     * @param player
     * @param dean
     */
    public void renderCollision(SpriteBatch uiBatch, List<Rectangle> worldCollision, Player player, Dean dean,
            Dean librarian) {
        for (Rectangle rectangle : worldCollision) {
            uiBatch.setColor(1, 0, 0, 0.75f);
            uiBatch.draw(missingTexture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        uiBatch.draw(missingTexture, player.getHitbox().x + 16, player.getHitbox().y + 16, player.getHitbox().width,
                player.getHitbox().height);
        uiBatch.draw(missingTexture, dean.getReachRectangle().x + 16, dean.getReachRectangle().y + 16,
                dean.getReachRectangle().width, dean.getReachRectangle().height);
        uiBatch.draw(missingTexture, librarian.getReachRectangle().x + 16, librarian.getReachRectangle().y + 16,
                librarian.getReachRectangle().width, librarian.getReachRectangle().height);
    }

    /**
     * Render the zones for triggers (dev mode).
     * 
     * @param uiBatch - The SpriteBatch used for this (should be the ui batch).
     */
    public void renderTriggers(SpriteBatch uiBatch) {
        for (Trigger t : TriggerSystem.getTriggers()) {
            Rectangle rectangle = t.getZone();
            uiBatch.setColor(0, 1, 1, 0.75f);
            uiBatch.draw(missingTexture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Display the pause overlay, with instructions and controls.
     * 
     * @param screenWidth          - how many pixels wide the screen is.
     * @param screenHeight         - how many pixels high the screen is.
     * @param positiveEventCounter - Number of PowerUps collected.
     * @param negativeEventCounter - Number of times caught by the dean.
     * @param hiddenEventCounter   - Number of secrets found.
     */
    public void renderPauseOverlay(int screenWidth, int screenHeight, int positiveEventCounter,
            int negativeEventCounter, int hiddenEventCounter) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.75f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.draw(uiBatch, "Escape from Uni", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.draw(uiBatch, "Instructions", screenWidth / 2f, (screenHeight / 2f) - 120);
        font.draw(uiBatch, "Stats", screenWidth / 2f, (screenHeight / 2f) - 200);
        font.getData().setScale(1f);
        renderControls(screenWidth, screenHeight);
        renderStats(screenWidth, screenHeight, positiveEventCounter, negativeEventCounter, hiddenEventCounter);

        uiBatch.end();
    }

    /**
     * Display the start overlay, with instructions, controls and how to start the
     * game.
     * 
     * @param screenWidth  - how many pixels wide the screen is.
     * @param screenHeight - how many pixels high the screen is.
     */
    public void renderStartOverlay(int screenWidth, int screenHeight) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.75f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.draw(uiBatch, "Escape from Uni", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.draw(uiBatch, "Instructions", screenWidth / 2f, (screenHeight / 2f) - 120);

        font.setColor(0, 1, 1, 1);
        font.draw(uiBatch, "Press Space to Start!", screenWidth / 2f, (screenHeight / 2f) - 200);
        font.setColor(1, 1, 1, 1);

        font.getData().setScale(1f);
        renderControls(screenWidth, screenHeight);
        font.draw(uiBatch, "Avoid the dean and escape the maze in time!", screenWidth / 2f, (screenHeight / 2f) - 160);

        uiBatch.end();
    }

    /**
     * Render the controls list, tellign you all the buttons and what they do.
     * 
     * @param screenWidth  - how many pixels wide the screen is.
     * @param screenHeight - how many pixels high the screen is.
     */
    private void renderControls(int screenWidth, int screenHeight) {
        font.draw(uiBatch, "Press P to pause / resume!", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 20);
        font.draw(uiBatch, "Press E to interact.", screenWidth / 2f, (screenHeight / 2f) - 40);
        font.draw(uiBatch, "Use WASD or arrow keys to move.", screenWidth / 2f, (screenHeight / 2f) - 60);
        font.draw(uiBatch, "Press P to pause!", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press F11 to Fullscreen.", screenWidth / 2f, (screenHeight / 2f) - 80);
    }

    /**
     * Render the Stats at the bottom of the overlay, showing PowerUps collected,
     * times caught and secrets found.
     * 
     * @param screenWidth          - how many pixels wide the screen is.
     * @param screenHeight         - how many pixels high the screen is.
     * @param positiveEventCounter - Number of PowerUps collected.
     * @param negativeEventCounter - Number of times caught by the dean.
     * @param hiddenEventCounter   - Number of secrets found.
     */
    private void renderStats(int screenWidth, int screenHeight, int positiveEventCounter, int negativeEventCounter,
            int hiddenEventCounter) {
        font.draw(uiBatch, "PowerUps Collected: " + positiveEventCounter, screenWidth / 2f, (screenHeight / 2f) - 240);
        font.draw(uiBatch, "Dean Captures: " + negativeEventCounter, screenWidth / 2f, (screenHeight / 2f) - 260);
        font.draw(uiBatch, "Secrets Found: " + hiddenEventCounter, screenWidth / 2f, (screenHeight / 2f) - 280);
    }

    /**
     * Display the win overlay, with your score and how much time was left.
     * 
     * @param screenWidth          - How many pixels wide the screen is.
     * @param screenHeight         - How many pixels high the screen is.
     * @param timeLeft             - How much time was left when the player escaped.
     * @param score                - the score the player managed to get.
     * @param positiveEventCounter - Number of PowerUps collected.
     * @param negativeEventCounter - Number of times caught by the dean.
     * @param hiddenEventCounter   - Number of secrets found.
     */
    public void renderWinOverlay(int screenWidth, int screenHeight, float timeLeft, int score, int positiveEventCounter,
            int negativeEventCounter, int hiddenEventCounter) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.75f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.draw(uiBatch, "Stats", screenWidth / 2f, (screenHeight / 2f) - 200);
        font.setColor(0, 1, 1, 1);
        font.draw(uiBatch, "YOU WIN!", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.setColor(1, 1, 1, 1);
        font.getData().setScale(1f);
        font.draw(uiBatch, TimerSystem.getClockDisplay(), screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Score: " + Integer.toString(score) + "  (Time remaining + Achievement Bonus)",
                screenWidth / 2f, (screenHeight / 2f) - 20);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 40);
        font.draw(uiBatch, "Press SPACE to restart.", screenWidth / 2f, (screenHeight / 2f) - 60);
        font.draw(uiBatch, Main.leaderBoard.toString(), screenWidth * 0.1f, screenHeight * 0.5f);
        if (firstLeaderBoard) {
            font.draw(uiBatch, "Enter Name:", screenWidth * 0.1f, screenHeight / 2f + 70);
        }
        renderStats(screenWidth, screenHeight, positiveEventCounter, negativeEventCounter, hiddenEventCounter);

        AchievementSystem.draw(new Vector2(10f, screenHeight - 10f), 3, uiBatch, font);
        uiBatch.end();

        // Leaderboard text input
        stage.setKeyboardFocus(textfield);
        textfield.setFocusTraversal(false);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (firstLeaderBoard && textfield.hasKeyboardFocus() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            String name = textfield.getText();
            Main.leaderBoard.addEntry(name, Main.calculateScore());
            textfield.setVisible(false);
            textfield.setText(null);
            stage.unfocus(textfield);
            textfield.setDisabled(true);
            firstLeaderBoard = false;
        }
    }

    /**
     * Display the lose overlay, for when you run out of time.
     * 
     * @param screenWidth          - How many pixels wide the screen is.
     * @param screenHeight         - How many pixels high the screen is.
     * @param positiveEventCounter - Number of PowerUps collected.
     * @param negativeEventCounter - Number of times caught by the dean.
     * @param hiddenEventCounter   - Number of secrets found.
     */
    public void renderLoseOverlay(int screenWidth, int screenHeight, int positiveEventCounter, int negativeEventCounter,
            int hiddenEventCounter) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.75f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.draw(uiBatch, "Stats", screenWidth / 2f, (screenHeight / 2f) - 200);
        font.setColor(1, 0, 0, 1);
        font.draw(uiBatch, "TIME IS UP!", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.setColor(1, 1, 1, 1);
        font.getData().setScale(1f);
        font.draw(uiBatch, "Better luck next time.", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 40);
        renderStats(screenWidth, screenHeight, positiveEventCounter, negativeEventCounter, hiddenEventCounter);
        uiBatch.end();
    }

    /**
     * <P>
     * Moves the camera to the show the current room.
     * </P>
     * <P>
     * This should only be called from the loadRoom method in Main
     * </P>
     *
     * @param x              - The x coordinate of the room.
     * @param y              - The y coordinate of the room.
     * @param viewportWidth  - The viewport width.
     * @param viewportHeight - The viewport height.
     */
    public void loadRoom(int x, int y, int viewportWidth, int viewportHeight) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, viewportWidth, viewportHeight);
        this.camera.translate(x * viewportWidth, y * viewportHeight);
        this.camera.update();
    }

    public void update() {
        if (!(darknessLeft <= 0)) {
            darknessLeft -= Gdx.graphics.getDeltaTime();
            if (darknessLeft <= 0) {
                ToastSystem.addToast("Your vision returns!");
                darknessActive = false;
            }
        }
    }

    public void activateDarkness() {
        darknessActive = true;
        darknessLeft = 20;
    }

}
