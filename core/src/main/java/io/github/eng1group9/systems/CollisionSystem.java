package io.github.eng1group9.systems;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles player collision.
 */
public class CollisionSystem {
    private List<Rectangle> worldCollision;
    private List<Rectangle> worldCollisionOriginal;
    private TiledMap map;
    private List<Rectangle> removedCollisions; // used for resetting

    /**
     * Take a tilemap and setup a list of collision rectangles using the "Collision" layer.
     * @param map - The tilemap (.tmx file).
     */
    public void init(TiledMap map) {
        this.map = map;
        MapLayer collisionLayer = map.getLayers().get("Collision");
        MapObjects collisionObjects = collisionLayer.getObjects();
        worldCollisionOriginal = new LinkedList<>();
        worldCollision = new LinkedList<>();
        removedCollisions = new ArrayList<>();
        for (MapObject mapObject : collisionObjects) {
            Rectangle nextRectangle = ((RectangleMapObject) mapObject).getRectangle();
            nextRectangle.set(nextRectangle.x * 2,nextRectangle.y * 2, nextRectangle.width * 2, nextRectangle.height * 2);
            worldCollisionOriginal.add(nextRectangle);
            worldCollision.add(new Rectangle(nextRectangle));
        }
    }

    public void reset(){
        worldCollision.addAll(removedCollisions);
    }

    public List<Rectangle> getWorldCollision() { return worldCollision; }

    /**
     * Serches collision Rectangle for name, then deletes from world collision by the rectangle.
     * @param name - The name of the rectangle.
     */
    public void removeCollisionByName(String name) {
        MapLayer collisionLayer = map.getLayers().get("Collision");
        MapObjects collisionObjects = collisionLayer.getObjects();

        for (MapObject m : collisionObjects) {

            if (m.getName() == null) {
                continue;
            }

            if (!m.getName().equals(name)) {
                continue;
            }

            Rectangle r = ((RectangleMapObject) m).getRectangle();
            worldCollision.remove(r);
            removedCollisions.add(r);
        }
    }

    /**
     * Check if it is safe for an entity to move to a given location.
     * @param x - The x co-ordinate to check.
     * @param y - The y co-ordinate to check.
     * @param currentHitbox - The hitbox of the entity you wish to check.
     * @return True if the entity will not hit collision when it moves there.
     */
    public boolean safeToMove(float x, float y, Rectangle currentHitbox) {
        Rectangle testHitbox = new Rectangle();
        testHitbox = testHitbox.set(currentHitbox);
        testHitbox.setPosition(x + 16, y + 16);

        for (Rectangle rectangle : worldCollision) {
            if (rectangle.overlaps(testHitbox)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <P>Adjusts the collision rectangles to the current room.</P>
     * <P>This should only be called from the loadRoom method in Main</P>
     *
     * @param x - The x coordinate of the room.
     * @param y - The y coordinate of the room.
     * @param viewportWidth - The viewport width.
     * @param viewportHeight - The viewport height.
     */
    public void loadRoom(int x, int y, int viewportWidth, int viewportHeight){
        worldCollision = new LinkedList<>();
        for (Rectangle originalRectangle : worldCollisionOriginal){
            float posX = originalRectangle.getX()-(x*viewportWidth*2);
            float posY = originalRectangle.getY()-(y*viewportHeight*2);

            Rectangle rectangle = new Rectangle(originalRectangle);
            rectangle.setPosition(posX, posY);
            worldCollision.add(rectangle);
        }
    }
}
