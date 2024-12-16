import os
import tty
import termios
import sys
import time

from utils.grid import make_grid, make_grid_with_points
from utils.read import read_input

example = """
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########
""".strip()

up = (0, -1)
down = (0, 1)
left = (-1, 0)
right = (1, 0)

dir_map = {'^': up, 'v': down, '<': left, '>': right}

def parse(input):
    return make_grid(input)

def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]

def sub(p1, p2):
    return p1[0] - p2[0], p1[1] - p2[1]

def get_key():
    fd = sys.stdin.fileno()
    old_settings = termios.tcgetattr(fd)
    try:
        tty.setraw(sys.stdin.fileno())
        ch = sys.stdin.read(1)
    finally:
        termios.tcsetattr(fd, termios.TCSADRAIN, old_settings)
    return ch


class Game:
    def __init__(self, start_state):
        self.grid = parse(start_state)
        self.start = None
        self.boxes = set()
        self.walls = set()
        self.x_size = self.grid._x_size * 2
        self.y_size = self.grid._y_size
        self.running = True
        for (px, py), v in self.grid:
            if v == '@':
                self.start = (px * 2, py)
            elif v == '#':
                self.walls.add((px * 2, py))
                self.walls.add((px * 2 + 1, py))
            elif v == 'O':
                self.boxes.add((px * 2, py))
        assert self.start is not None, 'Could not find start'
        self.pos = self.start

    def move(self, direction):
        d = direction
        boxes_to_move = []
        if d == right:
            next_pos = add(self.pos, d)
            while next_pos in self.boxes:
                boxes_to_move.append(next_pos)
                next_pos = add(next_pos, add(d, d))
        elif d == left:
            next_pos = add(self.pos, add(d, d))
            while next_pos in self.boxes:
                boxes_to_move.append(next_pos)
                next_pos = add(next_pos, add(d, d))
        elif d == up or d == down:
            next_pos = add(self.pos, d)
            if next_pos in self.boxes:
                boxes_to_move.append(next_pos)
            elif add(next_pos, left) in self.boxes:
                boxes_to_move.append(add(next_pos, left))

            iters = 0
            max_iters = 1000
            while iters < max_iters:
                next_pos = add(next_pos, d)
                nx, ny = next_pos
                found_box = False
                extra_boxes_to_move = []
                for bx, by in boxes_to_move:
                    if (d == up and by == ny + 1) or (d == down and by == ny - 1):
                        if (bx - 1, ny) in self.boxes:
                            extra_boxes_to_move.append((bx - 1, ny))
                            found_box = True
                        if (bx, ny) in self.boxes:
                            extra_boxes_to_move.append((bx, ny))
                            found_box = True
                        if (bx + 1, ny) in self.boxes:
                            extra_boxes_to_move.append((bx + 1, ny))
                            found_box = True
                        extra_boxes_to_move = list(set(extra_boxes_to_move))
                boxes_to_move += extra_boxes_to_move
                iters += 1
                if not found_box:
                    break

        blocked = False
        for box in boxes_to_move:
            if add(box, d) in self.walls or add(add(box, right), d) in self.walls:
                blocked = True
                break
        if add(self.pos, d) in self.walls: blocked = True
        if blocked:
            return

        for box in boxes_to_move:
            self.boxes.remove(box)
        for box in boxes_to_move:
            self.boxes.add(add(box, d))

        self.pos = add(self.pos, d)
        # print_grid(pos, walls, boxes, x_size, y_size)

    def handle_input(self, key):
        if key in ['w', 'up']:
            self.move(up)
        elif key in ['s', 'down']:
            self.move(down)
        elif key in ['a', 'left']:
            self.move(left)
        elif key in ['d', 'right']:
            self.move(right)
        elif key == 'q':
            self.running = False

def print_grid(robot_xy, walls, boxes, x_size, y_size):
    overlays = [(add(p, right), ']') for p in boxes]
    print(make_grid_with_points({'#': walls, '[': boxes, '@': {robot_xy}}, x_size, y_size,
                                default='.').as_string(overlays))

def clear_screen():
    os.system('clear')

def draw(game):
    clear_screen()
    print("Use WASD or arrow keys to move, 'Q' to quit")
    print_grid(game.pos, game.walls, game.boxes, game.x_size, game.y_size)


if __name__ == "__main__":
    g = Game(example)

    while g.running:
        draw(g)
        key = get_key()
        g.handle_input(key)
