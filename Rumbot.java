import java.util.*;
import java.io.*;
import java.math.*;


class Entity {
    int x;
    int y;
    int id;
    public Entity(int arg_id, int x1, int y1){
        id = arg_id;
        x = x1;
        y = y1;
    }
    
    public int distance(Entity other){
        int dx = x - other.x;
        int dy = y - other.y;
        return (int) Math.sqrt(dx^2+dy^2);
    }

    public Entity closest(Entity[] e){
        int min_dist = 9999999;
        Entity best = null;
        for (int i = 0 ; i < e.length ; i++){
            Entity ent = e[i];
            if (ent == null){
                continue;
            }
            int dist = this.distance(ent);
            if (dist < min_dist){
                min_dist = dist;
                best = ent;
            }
        }
        return best;
    }
}

class Ship extends Entity {
    
    int rotation;
    int speed;
    int rum;
    int owner;
    int mine_cooldown;
    int canon_cooldown;
    int attack_range;
    
    public Ship(int arg_id, int arg1, int arg2, int arg3, int arg4, int x1, int y1, int mc, int cbc) {
        super(arg_id, x1, y1);
        rotation = arg1;
        speed = arg2;
        rum = arg3;
        owner = arg4;
        mine_cooldown = mc;
        canon_cooldown = cbc;
        attack_range = 10;
    }

    public boolean canFire(){
        return canon_cooldown == 0;
    }

    public boolean canMine(){
        return mine_cooldown == 0;
    }

    public Barrel bestBarrel(Barrel[] b){
        int min_dist = 9999999;
        Barrel best = null;
        for (int i = 0 ; i < b.length ; i++){
            Barrel barrel = b[i];
            if (barrel == null){
                continue;
            }
            int dist = this.distance(barrel)/barrel.rum_count;
            if (dist < min_dist){
                min_dist = dist;
                best = barrel;
            }
        }
        return best;
    }

}


class Barrel extends Entity{
    int rum_count;
    public Barrel(int arg_id, int arg1, int x1, int y1){
        super(arg_id, x1, y1); //  cals construtor of Entity
        rum_count = arg1;
    }
}

class Move {
    Ship ship;
    Entity dest;
    int dist; 
    public Move(Ship s, Entity e, int d){
        ship = s;
        dest = e;
        dist = d;
    }
}

class Mine extends Entity {
    int damage_sides;
    int damage_center;
    public Mine(int arg_id, int x1, int y1){
        super(arg_id, x1, y1);
        damage_center = 50;
        damage_sides = 25;
    }
}

class CannonBall extends Entity {
    int damage_sides;
    int damage_center;
    int shipid;
    int turns;
    public CannonBall(int arg_id, int arg1, int arg2, int x1, int y1){
        super(arg_id, x1, y1);
        damage_center = 50;
        damage_sides = 25;
        shipid = arg1;
        turns = arg2;
    }
}

 
class Player {
    final static int ME = 1;
    final static int ENEMY = 0;
    final static int MINE_COOLDOWN = 4;
    final static int CANON_COOLDOWN = 1;
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int[] mine_cooldowns = {0, 0, 0};
        int [] cb_cooldowns = {0, 0, 0};

        // game loop
        while (true) {
            for (int i = 0 ; i< 3 ; i++){
                if(mine_cooldowns[i] > 0)
                    mine_cooldowns[i]--;
                if(cb_cooldowns[i] > 0)
                    cb_cooldowns[i]--;
            }
            int myShipCount = in.nextInt(); // the number of remaining ships
            Ship[] my_ships = new Ship[myShipCount];
            Ship[] enemy_ships = new Ship[myShipCount];
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            Barrel[] barrels = new Barrel[entityCount];
            Mine[] mines = new Mine[entityCount];
            CannonBall[] cannonballs = new CannonBall[entityCount];
            
            //fil lists with values
            int my_ships_stored = 0;
            int enemy_ships_stored = 0;
            int barrels_stored = 0;
            int mines_stored = 0;
            int cb_stored = 0;
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
                    if (arg4 == ME){
                        my_ships[my_ships_stored] = new Ship(entityId, arg1, arg2, arg3, arg4, x, y, mine_cooldowns[my_ships_stored], cb_cooldowns[my_ships_stored]);
                        my_ships_stored++;
                    }
                    else
                        enemy_ships[enemy_ships_stored++] = new Ship(entityId, arg1, arg2, arg3, arg4, x, y, 0, 0);
                }
                else if (entityType.equals("BARREL")){
                    barrels[barrels_stored++] = new Barrel(entityId, arg1,x,y);
                }
                else if (entityType.equals("MINE")){
                    mines[mines_stored++] = new Mine(entityId, x,y);
                }
                else if (entityType.equals("CANNONBALL")){
                    cannonballs[cb_stored++] = new CannonBall(entityId, arg1, arg2, x,y);
                }
            }


            for (int i = 0 ; i < myShipCount ; i++){
                boolean bombed = false; // tracks if we mined or fired
                Ship ship = my_ships[i];
                if (ship.canMine())   {
                    System.out.println("MINE");
                    bombed = true;
                    mine_cooldowns[i] = MINE_COOLDOWN + 1;
                }
                else if (ship.canFire()){
                    Ship[] in_range = new Ship[myShipCount];
                    int idx = 0;
                    for (int j = 0 ; j < myShipCount ; j++)
                        if (enemy_ships[j].distance(ship) < ship.attack_range)
                            in_range[idx++] = enemy_ships[j]; 
                    if (in_range.length > 0) {
                        Entity closest = ship.closest(in_range);
                        System.out.println("FIRE " + closest.x + " " + closest.y);
                        bombed = true;
                        cb_cooldowns[i] = CANON_COOLDOWN + 1;
                    }
                }
                else if (!bombed){
                    Barrel best = ship.bestBarrel(barrels);
                    System.out.println("MOVE " + best.x + " " + best.y);
                }
            }
        }
    }
}
