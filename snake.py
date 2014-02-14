#!/usr/bin/python
import os, curses, atexit, threading, time
from random import randint
from sys import exit

screen = curses.initscr()
curses.noecho()
curses.curs_set(0)
screen.keypad(1)
screen.leaveok(0)

foodX = 0
foodY = 0
#event = curses.KEY_RIGHT
eventList = []
exitVar = 0
snakePos = []

def place_Food():
    global foodX, foodY
    maxY, maxX = screen.getmaxyx()
    foodX = randint(0,maxX-1)
    foodY = randint(0,maxY-1)
    foodX = int(2 * round(float(foodX)/2))
    try:
        screen.addch(foodY, foodX, 'x')
        screen.refresh()
    except:
        place_Food()

def move_Snake():
    global exitVar
    global snakePos
    global foodX, foodY
    clearY = 0
    clearX = 0
    y = 0
    x = 2
    length = 1
    
    lastEvent = curses.KEY_RIGHT
    snakePos.append((y,x))
    while 1:
        maxY,maxX = screen.getmaxyx()
        screen.addch(clearY,clearX, ' ')
        
        
        if len(eventList) == 0:
            event = lastEvent
        else:
            event = eventList[0]
            eventList.pop(0)
            lastEvent = event
        
        if event == curses.KEY_LEFT:
            x -= 2
            if x < 0:
                curses.endwin()
                print("You Lose")
                exitVar = 1
                exit()
            screen.addch(y,x,'o')
        elif event == curses.KEY_RIGHT:
            x += 2
            if x >= maxX:
                curses.endwin()
                print("You Lose")
                exitVar = 1
                exit()
            screen.addch(y,x,'o')
        elif event == curses.KEY_DOWN:
            y += 1
            if y >= maxY:
                curses.endwin()
                print("You Lose")
                exitVar = 1
                exit()
            screen.addch(y,x,'o')
        elif event == curses.KEY_UP:
            y -= 1
            if y < 0:
                curses.endwin()
                print("You Lose")
                exitVar = 1
                exit()
            screen.addch(y,x,'o')
        elif event == 27:
            curses.endwin()
            exitVar = 1
            curses.endwin();
            exit()
        screen.refresh()
        if x == foodX and y == foodY:
            length += 1
            place_Food()
        if length == len(snakePos):
            clearY, clearX = snakePos.pop(0)
        checkCollision(y,x)
        if exitVar == 1:
            curses.endwin()
            exit(0)
        snakePos.append((y,x))
        screen.addstr(20,0,str(snakePos[0]))
        time.sleep(.25)

def checkCollision(newY,newX):
    for y,x in snakePos:
        if y == newY and x == newX:
            curses.endwin()
            print("You Lose")
            exitVar = 1
            exit(0)



if __name__ == "__main__":
    os.system('clear')
    event = curses.KEY_RIGHT
    
    
    movement = threading.Thread(target=move_Snake, args=())
    movement.daemon = True
    movement.start()
    
    place_Food()
    
    while exitVar == 0:
        eventList.append(screen.getch())
        if exitVar == 1:
            break;

    curses.endwin()
    exit(0)