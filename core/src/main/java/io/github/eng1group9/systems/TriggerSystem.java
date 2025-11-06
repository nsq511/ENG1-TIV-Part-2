package io.github.eng1group9.systems;

import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;

import io.github.eng1group9.Main;
import io.github.eng1group9.entities.Player;

/**
 * The system used to make things happen if a player enters a given area.
 * The Triggers layer on the TileMap contains Rectangles used to denote these areas.
 * The name of the Rectangle is its ID, which is used to determine what happens when it is triggered.
 */
public class TriggerSystem {

    private static List<RectangleMapObject> triggers = new LinkedList<>();

    public static void init(String tmxPath) {
        triggers = getTriggers(tmxPath);
    }

    public static List<RectangleMapObject> getTriggers(String tmxPath) {
        TiledMap map = new TmxMapLoader().load(tmxPath);
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        MapObjects triggerObjects = triggerLayer.getObjects();
        for (MapObject mapObject : triggerObjects) {
            RectangleMapObject t = (RectangleMapObject) mapObject;
            Rectangle nextRectangle = t.getRectangle();
            nextRectangle.set(nextRectangle.x * 2,nextRectangle.y * 2, nextRectangle.width * 2, nextRectangle.height * 2);
            triggers.add(t);
            System.out.println("Loaded Trigger " + t.getName());
        }
        return triggers;
    }

    public static List<RectangleMapObject> getTriggers() {
        return triggers;
    }

    /**
     * Will check if the given player is staninding in any triggers, and trigger them if so
     * @param player The player which is being checked
     */
    public static void check(Player player) {
        for (RectangleMapObject t : triggers) {
            if (player.isColliding(t.getRectangle())) {
                System.out.println("Triggered " + t.getName() + "!");
                trigger(Integer.parseInt(t.getName()), player);
            }
        }
    }

    /**
     * Will act based on which trigger has been activated
     * @param ID the trigger that has been activated
     */
    public static void trigger(int ID, Player player) {
        switch (ID) {
            case 0: // open the main door
                Main.openExit();
                break;
            case 1: // Get the chest room key
                player.giveChestRoomKey();
                break;
            case 2: // Get the scroll
                
                break;
            case 3: // Standing by the switch 
                
                break;
            case 4: // Standing by the mouse hole
                
                break;
            case 5: // Standing by the chest
                player.giveExitKey();
                break;
            case 6: // Standing by the chest room door
                Main.openChestRoomDoor();
                break;
            default:
                break;
        }
    }
}
