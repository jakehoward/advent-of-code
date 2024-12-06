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


def is_loop(grid, start_pos, start_direction, synthetic_obstacle_point):
    seen = set()

    def is_obstacle(p):
        return grid.at_p(p) == '#' or p == synthetic_obstacle_point

    direction = start_direction
    pos = start_pos
    while grid.in_bounds_p(pos):
        if (pos, direction) in seen:
            return True
        seen.add((pos, direction))

        next_pos = add(pos, direction)
        if grid.in_bounds_p(next_pos) and is_obstacle(next_pos):
            direction = next_direction(direction)
        else:
            pos = next_pos

    return False


def get_pos_on_right(pos, direction):
    if direction == up:
        return pos[0] + 1, pos[1]
    if direction == down:
        return pos[0] - 1, pos[1]
    if direction == right:
        return pos[0], pos[1] + 1
    if direction == left:
        return pos[0], pos[1] - 1


def part2(input):
    grid = make_grid(input)
    print('Grid size', grid._x_size, "x", grid._y_size)
    guard_start_idx = grid._data.index('^')
    guard_start = (guard_start_idx % grid._x_size, guard_start_idx // grid._y_size)
    pos = guard_start
    direction = up
    num_loops = 0
    # Done: Check coords of obstacle in example are coords found by algo (they are)
    # todo: consider if you're allowed to bounce of two hashes immediately doing a 180 turn?
    #       done: Lowers number of loops (still too high)
    # todo: removing the next_pos == guard_start constraint doesn't change the answer, is this suspicious?
    while grid.in_bounds_p(pos):
        next_pos = add(pos, direction)
        if grid.in_bounds_p(next_pos):
            if grid.at_p(next_pos) == '#':
                direction = next_direction(direction)
                continue
            elif next_pos != guard_start:
                pos_on_right = get_pos_on_right(pos, direction)
                if grid.in_bounds_p(pos_on_right) and grid.at_p(pos_on_right) != '#' and is_loop(grid, pos, direction,
                                                                                                 next_pos):
                    num_loops += 1
        pos = next_pos
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
        part2(input)


if __name__ == "__main__":
    run()
