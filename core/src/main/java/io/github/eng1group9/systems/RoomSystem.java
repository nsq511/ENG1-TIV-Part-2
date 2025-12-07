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


public class RoomSystem {

    public static boolean canUse = true;

    static class Door{
        private int id;
        private boolean locked;
        private Rectangle zone;
        private Rectangle originalZone;
        private Vector2 roomCoordinates;
        private int transitionID;

        public Door(int id, int transitionID, boolean locked, Rectangle zone, Vector2 roomCoordinates) {
            this.id = id;
            this.zone = zone;
            this.locked = locked;
            this.originalZone = new Rectangle(zone);
            this.roomCoordinates = roomCoordinates;
            this.transitionID = transitionID;
        }

        public void moveZone(int x,int y){
            this.zone.setPosition(originalZone.getX()+x,originalZone.getY()+y);
        }

        public int getID() {
            return id;
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

    private static List<Door> doors = new LinkedList<>();

    public static void init(String tmxPath, int viewportWidth, int viewportHeight){
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

    private static Door getDoor(int ID){
        Door door = null;
        for (Door d : doors) {
            if (d.id==ID){
                door = d;
            }
        }
        return door;
    }

    public static void loadRoom(int x,int y, int viewportWidth, int viewportHeight){
        for(Door door : doors){
            int posX = -(x*viewportWidth*2);
            int posY = -(y*viewportHeight*2);

            door.moveZone(posX,posY);
        }
        //canUse = true;
    }

    public static void checkDoor(Player player){
        for(Door door : doors){
            if(door.playerInZone(player) && canUse){
                useDoor(door,player);
            }
        }
    }

    private static void useDoor(Door door, Player player){
        canUse = false;
        Door transitionDoor =  getDoor(door.transitionID);
        int ID = door.getID();

        float playerPosX = transitionDoor.originalZone.getX()-(transitionDoor.getRoomX()*RenderingSystem.getViewportWidth()*2);
        float playerPosY = transitionDoor.originalZone.getY()-(transitionDoor.getRoomY()*RenderingSystem.getViewportHeight()*2);

        Vector2 playerPosition = new Vector2(playerPosX, playerPosY);

        if(player.hasLockpick()){
            Main.loadRoom(transitionDoor.roomCoordinates, playerPosition);
        }
    }
}
