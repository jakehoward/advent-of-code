from pathlib import Path

from utils.misc import timer
from utils.read import read_input
from utils.grid import make_grid

example = """....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#..."""

up = (0, -1)
right = (1, 0)
down = (0, 1)
left = (-1, 0)

def add(p, p2):
    return p[0] + p2[0], p[1] + p2[1]

def next_direction(direction):
    if direction == up:
        return right
    if direction == right:
        return down
    if direction == down:
        return left
    if direction == left:
        return up


def part1(input):
    grid = make_grid(input)
    guard_start_idx = grid._data.index('^')
    guard_start = (guard_start_idx % grid._x_size, guard_start_idx // grid._y_size)
    pos = guard_start
    direction = up
    seen = set()
    while grid.in_bounds(pos[0], pos[1]):
        seen.add(pos)
        next_pos = add(pos, direction)
        if grid.in_bounds(next_pos[0], next_pos[1]) and grid.at(next_pos[0], next_pos[1]) == '#':
            direction = next_direction(direction)
        else:
            pos = next_pos
    answer = len(seen)
    print(f'Pt1::ans: {answer}')

def is_square_loop(grid, pos, direction):
    debug = False
    if pos == (4, 8):
        debug = True
    # do you end up back where you started after 3 turns?
    og_dir = direction
    og_pos = pos
    turns = 0
    while grid.in_bounds(pos[0], pos[1]) and turns < 3:
        next_pos = add(pos, direction)
        if grid.in_bounds(next_pos[0], next_pos[1]) and grid.at(next_pos[0], next_pos[1]) == '#':
            direction = next_direction(direction)
            turns += 1
        else:
            pos = next_pos
    print('Turns:', turns, 'og dir:', og_dir, 'pos:', pos, 'og_pos', og_pos)
    if turns == 3:
        if direction == up or direction == down:
            print('ans:',pos[0] == og_pos[0])
            return pos[0] == og_pos[0]
        else:
            print('ans2:',pos[1] == og_pos[1])
            return pos[1] == og_pos[1]
    else:
        return False

def is_loop(grid, start_pos, start_direction):
    seen = set()
    seen.add((start_pos, start_direction))

    direction = next_direction(start_direction)
    pos = start_pos
    max_iters = 1000000
    iters = 0
    turns = 0
    while grid.in_bounds(pos[0], pos[1]): # and iters < max_iters:
        if iters > max_iters:
            return False #raise Exception('FORCE BREAKING')
        iters += 1
        if (pos, direction) in seen: # and turns >= 25: # As you increase the num turns, the num results goes down (some eventually exit)
            # return (pos, direction) == (start_pos, start_direction)
            return True
        # Why does it over count when tracking everywhere we've been?
        # Why does it end up on the same path before getting back to start? (get bumped into a _different_ loop?)
        # Why does it hit max iters before exiting the puzzle grid? <--- THIS !!!
        #    It MUST end up in a loop (causing it to force break), which the seen set finds if we add all pos,dir tuples
        seen.add((pos, direction))

        next_pos = add(pos, direction)
        if grid.in_bounds(next_pos[0], next_pos[1]) and grid.at(next_pos[0], next_pos[1]) == '#':
            direction = next_direction(direction)
            turns += 1
        else:
            pos = next_pos

    return False

def part2(input):
    grid = make_grid(input)
    guard_start_idx = grid._data.index('^')
    guard_start = (guard_start_idx % grid._x_size, guard_start_idx // grid._y_size)
    pos = guard_start
    direction = up
    num_loops = 0
    travelled_in_dir = False
    while grid.in_bounds(pos[0], pos[1]):
        next_pos = add(pos, direction)
        if grid.in_bounds(next_pos[0], next_pos[1]):
            if grid.at(next_pos[0], next_pos[1]) == '#':
                direction = next_direction(direction)
                travelled_in_dir = False
                continue
            # Don't include guard start and don't allow full 180 turn
            elif next_pos != guard_start: # and travelled_in_dir:
                # Put obstacle in way and search for loop shape (only if wouldn't create double bounce)
                loop_next_dir = next_direction(direction)
                loop_next_pos = add(pos, loop_next_dir)
                loop_next_pos_in_bounds = grid.in_bounds(loop_next_pos[0], loop_next_pos[1])

                if loop_next_pos_in_bounds and grid.at(loop_next_pos[0], loop_next_pos[1]) != '#' and is_loop(grid, pos, direction):
                    num_loops += 1
        pos = next_pos
        travelled_in_dir = True
    answer = num_loops
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        part1(example)

    with timer():
        part1(input)

    with timer():
        part2(example)

    with timer():
        # 1394 too high
        # 1342 too high
        part2(input)

if __name__ == "__main__":
    run()