package io.github.eng1group9.systems;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
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
 * System used to manage doors to other rooms on the map.
 * The Doors layer in the tilemap contains rectangles
 * which are the zones where the player can interact with the door.
 * If the player is in the zone, they will use the door when 'E' is pressed
 * and appear in the centre of the zone of the door that it leads to and load the
 * room that the other door is in.
 * <p></p>
 * The name of the rectangle is in the form "ID,transitionID,locked",
 * where ID is the ID of the door, transitionID is the ID of the door that it
 * leads to, and locked is either U or L for unlocked or locked respectively.
 */
public class RoomSystem {

    /**
     * A door on the map that the player can use to traverse through rooms.
     */
    static class Door{
        private int id;
        private boolean locked;
        private Rectangle zone;
        private Vector2 roomCoordinates;
        private int transitionID;

        public Door(int id, int transitionID, boolean locked, Rectangle zone, Vector2 roomCoordinates) {
            this.id = id;
            this.zone = zone;
            this.locked = locked;
            this.roomCoordinates = roomCoordinates;
            this.transitionID = transitionID;
        }

        public void moveZone(int x,int y){
            this.zone.setPosition(zone.getX()+x,zone.getY()+y);
        }

        public int getID() {
            return id;
        }

        public Rectangle getZone() {
            return zone;
        }

        public void unlock() {
            locked = false;
            getDoor(this.transitionID).unlock();
        }

        public int getRoomX(){
            return (int)roomCoordinates.x;
        }

        public int getRoomY(){
            return (int)roomCoordinates.y;
        }

        public boolean playerInZone(Player player) {
            return player.isColliding(zone);
        }
    }

    private static float doorCooldown;
    private static float doorCooldownRemaining;
    private static List<Door> doors = new LinkedList<>();

    public static void init(String tmxPath, int viewportWidth, int viewportHeight, float doorCooldownLength){
        doorCooldown = doorCooldownLength;
        doorCooldownRemaining = doorCooldownLength;
        TiledMap map = new TmxMapLoader().load(tmxPath);
        MapLayer doorLayer = map.getLayers().get("Doors");
        MapObjects doorObjects = doorLayer.getObjects();

        for (MapObject mapObject : doorObjects) {
            RectangleMapObject recMapObj = (RectangleMapObject) mapObject;
            try{
                int ID = Integer.parseInt(recMapObj.getName().split(",")[0]);
                int transitionID = Integer.parseInt(recMapObj.getName().split(",")[1]);
                String locked = recMapObj.getName().split(",")[2];

                Rectangle zone = recMapObj.getRectangle();
                zone.set(zone.x * 2, zone.y * 2, zone.width * 2, zone.height * 2);

                int roomX = ((int)zone.getX())/(viewportWidth*2);
                int roomY = ((int)zone.getY())/(viewportHeight*2);
                Vector2 roomCoordinates =  new Vector2((float)roomX, (float)roomY);
                Door d = new Door(ID, transitionID, locked.equals("L"), zone, roomCoordinates);
                doors.add(d);
                System.out.println("Initialised door " + ID + " to " + transitionID + " with coordinates " + roomCoordinates + "and X: " + zone.getX() + " Y: " + zone.getY());
            }
            catch(Exception e){
                System.out.println("Failed to initialise door");
            }
        }
    }

    /**
     * @return the door with that ID
     * @param ID the ID of the door
     */
    public static Door getDoor(int ID){
        Door door = null;
        for (Door d : doors) {
            if (d.id==ID){
                door = d;
            }
        }
        return door;
    }

    /**
     * @return A list of all doors in the system.
     */
    public static List<Door> getDoors(){
        return doors;
    }

    /**
     * Moves the door
     * @param offsetX
     * @param offsetY
     */
    public static void loadRoom(int offsetX, int offsetY){
        int x = -offsetX;
        int y = -offsetY;
        for(Door door : doors){
            door.moveZone(x,y);
        }
    }

    /**
     * Checks if the player is standing in front of a door and can therefore interact with it.
     * @param player The player which is being checked.
     */
    public static void checkDoors(Player player){
        for(Door door : doors){
            if(door.playerInZone(player)){
                useDoor(door,player);
            }
        }
    }

    /**
     * Decrements the cooldown to use doors.
     */
    public static void checkCooldown(){
        float delta = Gdx.graphics.getDeltaTime();
        doorCooldownRemaining -= delta;
    }

    /**
     * Uses the door in question if it is unlocked or if the player has a key/lockpick.
     * Will teleport the player to the other door connected to the door in question.
     * @param door The door in question.
     * @param player The player.
     */
    private static void useDoor(Door door, Player player){
        Door transitionDoor =  getDoor(door.transitionID);

        if(player.hasLockpick()){
            System.out.println("a");

            if(doorCooldownRemaining <= 0){
                doorCooldownRemaining = doorCooldown;
                System.out.println(transitionDoor.roomCoordinates);
                Main.loadRoom(transitionDoor.roomCoordinates);
                float playerPosX = transitionDoor.zone.getX();
                float playerPosY = transitionDoor.zone.getY();
                player.setPosition(playerPosX,playerPosY);
            }

        }
    }

}
