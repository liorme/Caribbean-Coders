# imports
import sys
import math

#Constants

SHIP_ATTACK_RANGE = 10
ME = 1
ENEMY = 0
MINE_CLDN = 4
CB_CLDN = 1
DAMAGE_CENTER = 50
DAMAGE_SIDES = 25
MINE_DMG = 25
MINE_ADJ_DMG = 10

class Location:
	def __init__ (self, x, y):
		self.x = x
		self.y = y

	def hex_dist(other):
		dx = math.abs(self.x-other.x)
		dy = math.abs(self.y-other.y)
		return max(dx+dy/2, dy)

class Ship:
	def __init__(self, id, arg1, arg2, arg3, arg4, x, y, mc=0, cbc=0):
		self.id = id
		self.rotation = arg1
		self.speed = arg2
		self.rum = arg3
		self.owner = arg4
		self.loc = Location(x,y)
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
			dist = hex_dist(self, barrel)/barrel.rum_count
			if dist < min_dist:
				dist = min_dist
				best = barrel
		return best

class Barrel:
	def __init__(self, id, arg1, x, y):
		self.id = id
		self.rum_count = arg1
		self.loc = Location(x,y)

class Mine:
	def __init__(self, id, x, y):
		self.id = id
		self.loc = Location(x,y)

class CanonBall:
	def __init__(self, id, arg1, arg2, x, y):
		self.id = id
		self.shipid = arg1
		self.turns = arg2
		self.loc = Location(x,y)

class Action:
	def __init__(self, type, loc=Location(-1, -1)):
		self.type = type
		self.loc = loc

class Plan:
	def __init__(self, ship, dest, plan):
		self.ship = ship
		self.dest = dest
		self.plan = plan # list of Actions
		self.step = 0
		
	def next_move(self):
		next = self.plan[self.step]
		self.step += 1
		return next

#global varaibles

plans = []

#functions

def find_plan(ship):
	for plan in plans:
		if plan.ship == ship:
			return plan
	return None

def do_plan(plan):
	next = plan.next_move()
	if next.type == "MOVE" or next.type == "FIRE":
		print "%s %d %d\n", next.type, next.loc.x, next.loc.y
	else:
		print next.type

def make_plan(ship):
	return Plan(ship, Location(0,0), [Action("MINE")])

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
        arg1 = int(arg_1)
        arg2 = int(arg_2)
        arg3 = int(arg_3)
        arg4 = int(arg_4)
        if entity_type == "SHIP":
        	if arg4 == ME:
        		my_ships.append(Ship(entity_id, arg1, arg2, arg3, arg4, x, y))
        	else:
        		enemy_ships.append(Ship(entity_id, arg1, arg2, arg3, arg4, x, y))
        elif entity_type == "BARREL":
        	barrels.append(Barrel(entity_id, arg1, x, y))
        elif entity_type == "MINE":
        	mines.append(Mine(entity_id, x, y))
        elif entity_type == "CANNONBALL":
        	cannonballs.append(CanonBall(entity_id, arg1, arg2, x, y))

    for ship in my_ships:
    	plan = find_plan(ship)
    	if plan is not None:
    		do_plan(plan)
    	else:
	    	plan = make_plan(ship)
	    	plans.append(plan)
	    	do_plan(plan)




