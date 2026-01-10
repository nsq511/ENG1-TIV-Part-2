package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.eng1group9.systems.RenderingSystem;

public class ProjectileWarning extends Entity {
    private float warningLength;

    public ProjectileWarning(Vector2 position, Character moveDirection, float warningLength) {
        super(position, 32, 32, new Vector2(0,0));

        setTexture(new Texture(Gdx.files.internal("Projectile/warning.png")));
        float width = RenderingSystem.getViewportWidth()*2;
        float height = RenderingSystem.getViewportHeight()*2;
        this.warningLength = warningLength;
        setScale(1);

        switch (moveDirection) {
            case 'U':
                setY(0);
                break;
            case 'D':
                setY(height-getHeight());
                break;
            case 'L':
                setX(width-getWidth());
                break;
            case 'R':
                setX(0);
                break;
        }
    }

    public float getWarningLength() {
        float delta = Gdx.graphics.getDeltaTime();
        return warningLength -= delta;
    }
}
