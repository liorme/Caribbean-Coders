import java.util.*;
import java.io.*;
import java.math.*;




class Ship {
    
    int rotation;
    int speed;
    int rum;
    int owner;
    int x;
    int y;
    
    public Ship(int arg1, int arg2, int arg3, int arg4, int x1, int y1) {
        rotation = arg1;
        speed = arg2;
        rum = arg3;
        owner = arg4;
        x = x1;
        y = y1;
    }

}

class Barrel {
    int rum_count;
    public int x;
    public int y;
    public Barrel(int arg1, int x1, int y1){
        rum_count = arg1;
        x = x1;
        y = y1;
    }
}

class Move {
    Ship ship;
    int dest_x;
    int dest_y;
}

 
 
class Player {
    static int ME = 1;
    static int ENEMY = 0;
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int myShipCount = in.nextInt(); // the number of remaining ships
            Ship[] ships = new Ship[myShipCount+1];
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            Barrel[] barrels = new Barrel[entityCount];
            int ships_stored = 0;
            int barrels_stored = 0;
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int x = in.nextInt();
                int y = in.nextInt();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                if (entityType.equals("SHIP")){
                    ships[ships_stored++] = new Ship(arg1, arg2, arg3, arg4, x, y);
                }
                else if (entityType.equals("BARREL")){
                    barrels[barrels_stored++] = new Barrel(arg1,x,y);
                }
            }
            for (int i = 0; i < myShipCount; i++) {
                if(barrels[0] == null)
                    System.out.println("barrels is null");
                int b = barrels[0].x;
                System.out.println("MOVE " + barrels[0].x + " " + barrels[0].y);
            }
        }
    }
}
