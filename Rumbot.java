# imports
import sys
import math
import time

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
    def __init__(self, x, y, z, r, ns, s, d, command):  # (x,y,z) int-rotation int-NextRotation int-speed int-distance str-FirstCommand
        self.x = x
        self.y = y
        self.z = z
        self.r = r
        self.ns = ns
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

def do_turn(ship):
    rship = ship
    if ship.rotation == 0:
        rship.x = ship.x + 1
        rship.y = ship.y
    elif ship.rotation == 1:
        rship.x = ship.x + 1 + ship.y%2
        rship.y = ship.y
    elif ship.rotation == 2:
        rship.x = ship.x - 1 + ship.y%2
        rship.y = ship.y
    elif ship.rotation == 3:
        rship.x = ship.x - 1
        rship.y = ship.y
    elif ship.rotation == 4:
        rship.x = ship.x - 1 + ship.y%2
        rship.y = ship.y + 1
    elif ship.rotation == 5:
        rship.x = ship.x + 1 + ship.y%2
        rship.y = ship.y + 1
    return rship

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
    turn = 0
    needs_checking = []
    next_turn = []
    first_cords = to_cube_cords(ship)
    cube_dest = to_cube_cords(dest)

    empty_plan = PlanState(first_cords.x, first_cords.y, first_cords.z, ship.rotation, 0, ship.speed,0, "")
    for i in xrange(max(ship.speed - 1, 0)):
        empty_plan = do_plan_turn(empty_plan)

    # slow
    slow_speed = empty_plan
    slow_d = hex_cube_dist(slow_speed, cube_dest)
    needs_checking.append(
        PlanState(slow_speed.x, slow_speed.y, slow_speed.z, ship.rotation, ship.rotation, max(ship.speed - 1, 0), slow_d, "SLOWER"))

    # turning
    if ship.speed == 0:
        normal_speed = empty_plan
        normal_d = slow_d
    else:
        normal_speed = do_plan_turn(empty_plan)
        normal_d = hex_cube_dist(normal_speed, cube_dest)
    needs_checking.append(
        PlanState(normal_speed.x, normal_speed.y, normal_speed.z, ship.rotation, (ship.rotation + 1) % 6, ship.speed, normal_d, "STARBOARD"))
    needs_checking.append(
        PlanState(normal_speed.x, normal_speed.y, normal_speed.z, ship.rotation, (ship.rotation - 1) % 6, ship.speed, normal_d,"PORT"))

    # fast
    if ship.speed == 2:
        fast_speed = normal_speed
        fast_d = normal_d
    else:
        fast_speed = do_plan_turn(normal_speed)
        fast_d = hex_cube_dist(fast_speed, cube_dest)
    needs_checking.append(
        PlanState(fast_speed.x, fast_speed.y, fast_speed.z, ship.rotation, ship.rotation, min(ship.speed + 1, 2),fast_d, "FASTER"))

    compute_first_obstacles(cannonballs, mines, my_ships, enemy_ships, ship.id)
    needs_checking = filter(check, needs_checking)
    min_dist = 100
    for plan in needs_checking[:]:
        if plan.d < min_dist:
            this_turn = [plan]
            min_dist = plan.d
        elif plan.d == min_dist:
            this_turn.append(plan)

    while time.time() - start_time < 0.09:
        turn += 1
        print >> sys.stderr, this_turn[0].d
        needs_checking = []
        next_turn = []
        for plan in this_turn:
            plan.r = plan.ns
            headplan = do_plan_turn(plan)
            plan.r = (plan.r + 3) % 6
            butplan = do_plan_turn(plan)
            plan_body = [CubePoint(plan.x, plan.y, plan.z), CubePoint(headplan.x, headplan.y, headplan.z),CubePoint(butplan.x, butplan.y, butplan.z)]
            for body_part in plan_body:
                if hex_cube_dist(body_part, cube_dest) == 0:
                    print plan.command
                    return

        for plan in this_turn:
            for i in xrange(max(plan.s - 1, 0)):
                plan = do_plan_turn(plan)

            # slow
            slow_speed = plan
            slow_d = hex_cube_dist(slow_speed, cube_dest)
            needs_checking.append(PlanState(slow_speed.x, slow_speed.y, slow_speed.z, plan.r, plan.r, max(plan.s - 1, 0), slow_d, plan.command))

            # turning
            if plan.s == 0:
                normal_speed = plan
                normal_d = slow_d
            else:
                normal_speed = do_plan_turn(plan)
                normal_d = hex_cube_dist(normal_speed, cube_dest)
            needs_checking.append(PlanState(normal_speed.x, normal_speed.y, normal_speed.z, plan.r, (plan.r + 1) % 6,plan.s, normal_d, plan.command))
            needs_checking.append(PlanState(normal_speed.x, normal_speed.y, normal_speed.z, plan.r, (plan.r - 1) % 6, plan.s, normal_d, plan.command))

            # fast
            if plan.s == 2:
                fast_speed = normal_speed
                fast_d = normal_d
            else:
                fast_speed = do_plan_turn(normal_speed)
                fast_d = hex_cube_dist(fast_speed, cube_dest)
            needs_checking.append(PlanState(fast_speed.x, fast_speed.y, fast_speed.z, plan.r, plan.r,min(plan.s + 1, 2), fast_d, plan.command))

            compute_obstacles(cannonballs, turn)
            next_turn = filter(check, needs_checking)
            min_dist = 100
            for plan in needs_checking[:]:
                if plan.d < min_dist:
                    next_turn = [plan]
                    min_dist = plan.d
                elif plan.d == min_dist:
                    next_turn.append(plan)

            this_turn = next_turn
    print "MOVE %s %s" %(dest.x, dest.y)
    return


def compute_first_obstacles(cannonballs, mines, my_ships, enemy_ships,id):
    global obstacles
    obstacles = []
    for cannonball in cannonballs:
        if cannonball.turns == 1:
            obstacles.append(to_cube_cords(cannonball))
    for mine in mines:
        obstacles.append(to_cube_cords(mine))
    for ship in my_ships+enemy_ships:
        if ship.id != id:
            backwards_ship = ship
            backwards_ship.rotation = (ship.rotation+3)%6
            obstacles.append(to_cube_cords(do_turn(ship)))
            obstacles.append(to_cube_cords(ship))
            obstacles.append(to_cube_cords(do_turn(backwards_ship)))
    return

def compute_obstacles(cannonballs, turn):
    global obstacles
    for cannonball in cannonballs:
        if cannonball.turns-turn == 0:
            for obstacle in obstacles:
                cords = to_cube_cords(cannonball)
                if hex_cube_dist(obstacle, cords) == 0:
                    obstacles.remove(obstacle)
        elif cannonball.turns-turn == 1:
            obstacles.append(to_cube_cords(cannonball))
    return

def check(plan):
    global obstacles
    plan.r = plan.ns
    headplan = do_plan_turn(plan)
    plan.r = (plan.r + 3)%6
    butplan = do_plan_turn(plan)
    plan_body = [CubePoint(plan.x,plan.y,plan.z),CubePoint(headplan.x,headplan.y,headplan.z),CubePoint(butplan.x,butplan.y,butplan.z)]
    for point in plan_body:
        if point in obstacles:
            return False
    return True

# game loop
while True:
    start_time = time.time()
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

    for ship in my_ships:
        min_dist = sys.maxint
        best = None
        for barrel in barrels:
            dist = hex_dist(ship, barrel)
            if dist < min_dist:
                min_dist = dist
                best = barrel
        move(ship, best, cannonballs, mines, my_ships, enemy_ships)
