#imports
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
			dist = hex_dist(self, barrel)/barrel.rum_count
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
    def __init__(self,x,y,z,r,s,command):#(x,y,z) int-rotation int-speed str-FirstCommand 
        self.x = x
        self.y = y
        self.z = z
        self.r = r
        self.s = s
        self.command = command

#global varaibles

#functions

def hex_dist(a, b):
	dx = math.abs(a.x-b.x)
	dy = math.abs(a.y-b.y)
	return max(dx+dy/2, dy)


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
        # To debug: print >> sys.stderr, "Debug messages..."

        # Any valid action, such as "WAIT" or "MOVE x y"
        print "MOVE 11 10"
