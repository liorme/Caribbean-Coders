import sys
import math
import random

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.
my_ships = []
enemy_ships = []
super_old_ships = []
old_ships = []

# game loop
while True:
    my_ships = []
    enemy_ships = []
    barrels = []
    my_ship_count = int(raw_input())  # the number of remaining ships
    entity_count = int(raw_input())  # the number of entities (e.g. ships, mines or cannonballs)
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
            if arg_4 == 1:
                my_ships.append({"x":x,"y":y,"id":entity_id,"rotation":arg_1,"speed":arg_2,"rum":arg_3})
            else:
                enemy_ships.append({"x":x,"y":y,"id":entity_id,"rotation":arg_1,"speed":arg_2,"rum":arg_3})
        elif entity_type == "BARREL":
            barrels.append({"x":x,"y":y,"id":entity_id,"rum":arg_1})
    
    fire = None
    for ship in enemy_ships:
        for old_ship in old_ships:
            for super_old_ship in super_old_ships:
                if ship["x"] == old_ship["x"] == super_old_ship["x"] and ship["y"] == old_ship["y"] == super_old_ship["y"]:
                    fire = ship
            
    for ship in my_ships:
        if fire != None:
            print "FIRE %s %s" %(fire["x"], fire["y"])
        else:
            min_dist = 1<<32
            target_barrel = {"x":random.randint(0,20),"y":random.randint(0,20)}
            for barrel in barrels:
                if (max((barrel["x"] - ship["x"])**2.0 , (barrel["y"] - ship["y"])**2.0)**0.6)/ barrel["rum"] < min_dist:
                    min_dist = (max((barrel["x"] - ship["x"])**2.0,(barrel["y"] - ship["y"])**2.0)**0.6)/ barrel["rum"]
                    target_barrel = barrel
            print "MOVE %s %s" %(target_barrel["x"],target_barrel["y"])
    super_old_ships = old_ships
    old_ships = enemy_ships
