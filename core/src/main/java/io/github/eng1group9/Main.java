package io.github.eng1group9;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private FitViewport viewport;

    private boolean isFullscreen = false;


    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(160, 120);

        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    public void input() {
        // Process player inputs here
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (isFullscreen) {
                Gdx.graphics.setWindowedMode(1920, 1080);
                isFullscreen = false;
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                isFullscreen = true;
            }
        }
    }

    public void logic() {
        // Process game logic here

    }

    public void draw() {
        // Draw frame here
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centres the camera
    }
}
