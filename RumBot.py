# imports
import sys
import math

# Constants

SHIP_ATTACK_RANGE = 10
ME = 1
ENEMY = 0
MINE_CLDN = 4
CB_CLDN = 1
DAMAGE_CENTER = 50
DAMAGE_SIDES = 25
MINE_DMG = 25
MINE_ADJ_DMG = 10


class Ship:
    def __init__(self, id, arg_1, arg_2, arg_3, arg_4, x, y, mc=0, cbc=0):
        self.id = id
        self.rotation = arg_1
        self.speed = arg_2
        self.rum = arg_3
        self.owner = arg_4
        self.x = x
        self.y = y
        self.mc = mc
        self.cbc = cbc

    def can_fire():
        return self.cbc == 0

    def can_mine():
        return self.mc == 0

    def best_barrel(barrels):
        min_dist = sys.maxint()
        best = None
        for barrel in barrels:
            dist = hex_dist(self, barrel) / barrel.rum_count
            if dist < min_dist:
                dist = min_dist
                best = barrel
        return best


class Barrel:
    def __init__(self, id, arg_1, x, y):
        self.id = id
        self.rum_count = arg_1
        self.x = x
        self.y = y


class Mine:
    def __init__(self, id, x, y):
        self.id = id
        self.x = x
        self.y = y


class CanonBall:
    def __init__(self, id, arg_1, arg_2, x, y):
        self.id = id
        self.shipid = arg_1
        self.turns = arg_2
        self.x = x
        self.y = y


class PlanState:
    def __init__(self, x, y, z, r, s, command):  # (x,y,z) int-rotation int-speed int-distance str-FirstCommand 
        self.x = x
        self.y = y
        self.z = z
        self.r = r
        self.s = s
        self.d = d
        self.command = command


class CubePoint:
    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z


# global varaibles

obstacles = []


# functions

def hex_dist(a, b):
    a = to_cube_cords(a)
    b = to_cube_cords(b)
    dx = abs(a.x - b.x)
    dy = abs(a.y - b.y)
    dz = abs(a.z - b.z)
    return (dx + dy + dz) / 2


def hex_cube_dist(a, b):
    dx = abs(a.x - b.x)
    dy = abs(a.y - b.y)
    dz = abs(a.z - b.z)
    return (dx + dy + dz) / 2


def to_cube_cords(object):  # returns cube cords in topple
    x = object.x
    y = object.y
    xp = x - (y - (y & 1)) / 2
    zp = y
    yp = -(xp + zp)
    return CubePoint(xp, yp, zp)


def do_plan_turn(plan):
    if plan.r == 0:
        plan.x = plan.x + 1
        plan.y = plan.y - 1
        plan.z = plan.z
    elif plan.r == 1:
        plan.x = plan.x + 1
        plan.y = plan.y
        plan.z = plan.z - 1
    elif plan.r == 2:
        plan.x = plan.x
        plan.y = plan.y + 1
        plan.z = plan.z - 1
    elif plan.r == 3:
        plan.x = plan.x - 1
        plan.y = plan.y + 1
        plan.z = plan.z
    elif plan.r == 4:
        plan.x = plan.x - 1
        plan.y = plan.y
        plan.z = plan.z + 1
    elif plan.r == 5:
        plan.x = plan.x
        plan.y = plan.y - 1
        plan.z = plan.z + 1
    return plan


def move(ship, dest, cannonballs, mines, my_ships, enemy_ships):
    this_turn = []
    needs_checking = []
    next_turn = []
    first_cords = to_cube_cords(ship)
    cube_dest = to_cube_cords(dest)

    empty_plan = PlanState(first_cords.x, first_cords.y, first_cords.z, ship.rotation, ship.speed, "")
    for i in xrange(max(ship.speed - 1, 0)):
        empty_plan = do_plan_turn(empty_plan)

    # slow
    slow_speed = empty_plan
    slow_d = hex_cube_dist(slow_speed, cube_dest)
    needs_checking.append(
        PlanState(slow_speed.x, slow_speed.y, slow_speed.z, ship.rotation, ship.rotation, max(ship.speed - 1, 0),
                  slow_d, "SLOW"))

    # turning
    if ship.speed == 0:
        normal_speed = empty_plan
        normal_d = slow_d
    else:
        normal_speed = do_plan_turn(empty_plan)
        normal_d = hex_cube_dist(normal_speed, cube_dest)
    needs_checking.append(
        PlanState(normal_speed.x, normal_speed.y, normal_speed.z, ship.rotation, (ship.rotation + 1) % 6, ship.speed,
                  "PORT"))
    needs_checking.append(
        PlanState(normal_speed.x, normal_speed.y, normal_speed.z, ship.rotation, (ship.rotation - 1) % 6, ship.speed,
                  "STARBOARD"))

    # fast
    if ship.speed == 2:
        fast_speed = normal_speed
        fast_d = normal_d
    else:
        fast_speed = do_plan_turn(normal_speed)
        fast_d = hex_cube_dist(fast_speed, cube_dest)
    needs_checking.append(
        PlanState(fast_speed.x, fast_speed.y, fast_speed.z, ship.rotation, ship.rotation, min(ship.speed + 1, 2),
                  "FAST"))

    compute_first_obstacles(cannonballs, mines, my_ships, enemy_ships)
    next_turn = filter(needs_checking, check)


def compute_first_obstacles(cannonballs, mines, my_ships, enemy_ships):
    global obstacles
    for cannonball in cannonballs:
        if cannonball.turns == 1:
            obstacles.append(to_cube_cords(cannonball))
    for mine in mines:
        obstacles


def compute_obstacles(cannonballs, turn):


def check(plan):


# game loop
while True:
    my_ship_count = int(raw_input())  # the number of remaining ships
    entity_count = int(raw_input())  # the number of entities (e.g. ships, mines or cannonballs)
    my_ships = []
    enemy_ships = []
    barrels = []
    cannonballs = []
    mines = []

    for i in xrange(entity_count):
        entity_id, entity_type, x, y, arg_1, arg_2, arg_3, arg_4 = raw_input().split()
        entity_id = int(entity_id)
        x = int(x)
        y = int(y)
        arg_1 = int(arg_1)
        arg_2 = int(arg_2)
        arg_3 = int(arg_3)
        arg_4 = int(arg_4)
        if entity_type == "SHIP":
            if arg_4 == ME:
                my_ships.append(Ship(entity_id, arg_1, arg_2, arg_3, arg_4, x, y))
            else:
                enemy_ships.append(Ship(entity_id, arg_1, arg_2, arg_3, arg_4, x, y))
        elif entity_type == "BARREL":
            barrels.append(Barrel(entity_id, arg_1, x, y))
        elif entity_type == "MINE":
            mines.append(Mine(entity_id, x, y))
        elif entity_type == "CANNONBALL":
            cannonballs.append(CanonBall(entity_id, arg_1, arg_2, x, y))

    for i in xrange(my_ship_count):
        # Write an action using print
        a, b, c = to_cube_cords(Barrel(0, 0, 0, 0)), to_cube_cords(Barrel(0, 0, 1, 0)), to_cube_cords(
            Barrel(0, 0, 0, 1))

        print >> sys.stderr, (a.x, a.y, a.z), (b.x, b.y, b.z), (c.x, c.y, c.z)

        # Any valid action, such as "WAIT" or "MOVE x y"
        print "MOVE 11 10"
