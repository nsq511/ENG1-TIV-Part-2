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

import com.badlogic.gdx.math.Vector2;
import io.github.eng1group9.Main;
import io.github.eng1group9.entities.Player;

/**
 * The system used to make things happen if a player enters a given area.
 * The Triggers layer on the TileMap contains Rectangles used to denote these
 * areas.
 * The name of the Rectangle is in the form "ID,triggerType"
 * which is used to determine what happens when it is triggered, and how.
 *
 * triggerType must be in the form T or I
 * for Touch or Interact
 */
public class TriggerSystem {

    /**
     * A Trigger with a zone and a player it relates too.
     */
    static class Trigger {
        private int ID;
        private Rectangle zone;
        private Rectangle originalZone;
        private boolean activateOnTouch = false;

        public Trigger(int ID, boolean activateOnTouch, Rectangle zone) {
            this.ID = ID;
            this.activateOnTouch = activateOnTouch;
            this.zone = zone;
            this.originalZone = new Rectangle(zone);
        }

        public int getID() {
            return ID;
        }

        public boolean isActivateOnTouch() {
            return activateOnTouch;
        }

        public boolean isActivateOnInteract() {
            return !activateOnTouch;
        }

        public Rectangle getZone() {
            return zone;
        }

        public boolean playerInZone(Player player) {
            return player.isColliding(zone);
        }

        public void moveZone(int x, int y) {
            this.zone.setPosition(originalZone.getX() + x, originalZone.getY() + y);
        }
    }

    private static List<Trigger> touchTriggers = new LinkedList<>();
    private static List<Trigger> interactTriggers = new LinkedList<>();

    public static void init(String tmxPath) {
        List<Trigger> triggers = getTriggers(tmxPath);
        for (Trigger t : triggers) {
            if (t.isActivateOnTouch()) {
                touchTriggers.add(t);
            } else {
                interactTriggers.add(t);
            }
        }
    }

    /**
     * Return a list of all triggers in a tileset.
     * This should only be used when first loading the tileset.
     * 
     * @param tmxPath - The path to the tileset (.tmx file).
     * @return A list of all Triggers.
     */
    public static List<Trigger> getTriggers(String tmxPath) {
        TiledMap map = new TmxMapLoader().load(tmxPath);
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        MapObjects triggerObjects = triggerLayer.getObjects();
        List<Trigger> triggers = new LinkedList<>();

        for (MapObject mapObject : triggerObjects) {
            RectangleMapObject recMapObj = (RectangleMapObject) mapObject;
            int ID = Integer.parseInt(recMapObj.getName().split(",")[0]);

            Rectangle zone = recMapObj.getRectangle();
            zone.set(zone.x * 2, zone.y * 2, zone.width * 2, zone.height * 2);

            String triggerType = recMapObj.getName().split(",")[1];
            Trigger t = new Trigger(ID, triggerType.equals("T"), zone);
            triggers.add(t);
            System.out.println("Loaded Trigger " + t.getID());
        }
        return triggers;
    }

    /**
     * @return A list of all touch triggers in the system (Name = ID,T).
     */
    public static List<Trigger> getTouchTriggers() {
        return touchTriggers;
    }

    /**
     * @return A list of all touch triggers in the system (Name = ID,I).
     */
    public static List<Trigger> getInteractTriggers() {
        return interactTriggers;
    }

    /**
     * Remove a trigger from the system.
     * 
     * @param ID - The ID of the trigger which should be removed.
     * @return True if it was successful.
     */
    public static boolean remove(int ID) {
        for (Trigger t : touchTriggers) {
            if (t.getID() == ID) {
                touchTriggers.remove(t);
                return true;
            }
        }
        for (Trigger t : interactTriggers) {
            if (t.getID() == ID) {
                interactTriggers.remove(t);
                return true;
            }
        }
        return false;
    }

    /**
     * @return A list of all triggers in the system (both types).
     */
    public static List<Trigger> getTriggers() {
        List<Trigger> triggers = new LinkedList<>();
        triggers.addAll(touchTriggers);
        triggers.addAll(interactTriggers);
        return triggers;
    }

    /**
     * Will check if the given player is staninding in any triggers, and trigger
     * them if so.
     * 
     * @param player The player which is being checked.
     *               This is run each time the player presses E.
     */
    public static void checkInteractTriggers(Player player) {
        for (Trigger t : interactTriggers) {
            if (t.playerInZone(player)) {
                System.out.println("Triggered " + t.getID() + "!");
                trigger(t.getID(), player);
            }
        }
    }

    /**
     * Will check if the given player is staninding in any triggers, and trigger
     * them if so.
     * 
     * @param player The player which is being checked.
     *               This is run each frame.
     */
    public static void checkTouchTriggers(Player player) {
        for (Trigger t : touchTriggers) {
            if (t.playerInZone(player)) {
                trigger(t.getID(), player);
            }
        }
    }

    /**
     * <P>
     * Adjusts the trigger zones to the current room.
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
    public static void loadRoom(int x, int y, int viewportWidth, int viewportHeight) {
        for (Trigger trigger : getTriggers()) {
            int posX = -(x * viewportWidth * 2);
            int posY = -(y * viewportHeight * 2);

            trigger.moveZone(posX, posY);
        }
    }

    /**
     * Will act based on which trigger has been activated
     * 
     * @param ID the trigger that has been activated
     */
    public static void trigger(int ID, Player player) {
        switch (ID) {
            case 0: // open the main door
                Main.winGame();
                break;
            case 1: // Get the chest room key
                player.giveChestRoomKey();
                break;
            case 2: // Get the scroll
                Main.getScroll();
                break;
            case 3: // Standing by the switch
                Main.dropSpikes();
                break;
            case 4: // Standing by the mouse hole
                Main.checkForLongboi();
                break;
            case 5: // Standing by the chest
                player.giveExitKey();
                break;
            case 6: // Standing by the chest room door
                Main.openChestRoomDoor();
                break;
            case 7: // Standing by the chest room door
                Main.openExit();
                break;
            case 8: // Pickup red potion.
                player.giveRedPotion();
                break;
            case 9:
                Main.loadRoom(0, 1, new Vector2(30, 30), new Vector2(50, 50), new Character[] { 'U', 'D' },
                        new Vector2(50, 50), new Character[] { 'U', 'D' });
                break;
            case 10:
                Main.loadRoom(0, 0);
                break;
            case 11:
                Main.openOutsideRoomDoor();
                break;
            case 12:
                Main.getBook();
                break;
            case 13:
                RenderingSystem.hideLayer("Potion1");
                remove(13);
                Main.getPotion();
                break;
            default:
                break;
        }
    }
}
