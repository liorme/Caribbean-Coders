import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
    
        // game loop
        while (true) {
            int myShipCount = in.nextInt(); // the number of remaining ships
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            int[][] barrel_locations = new int[26][2];
            int j = 0;
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int x = in.nextInt();
                int y = in.nextInt();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                if(entityType.equals("BARREL")){
                    barrel_locations[j][0] = x;
                    barrel_locations[j][1] = y;
                }
            }
            for (int i = 0; i < myShipCount; i++) {
                int x = barrel_locations[0][0];
                int y = barrel_locations[0][1];

                System.out.println("MOVE " + x + " "+ y); 
            }
        }
    }
}
