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

def find_guard_positions(grid, guard_start, start_direction):
    pos = guard_start
    direction = start_direction
    seen = set()
    while grid.in_bounds(pos[0], pos[1]):
        seen.add(pos)
        next_pos = add(pos, direction)
        if grid.in_bounds(next_pos[0], next_pos[1]) and grid.at(next_pos[0], next_pos[1]) == '#':
            direction = next_direction(direction)
        else:
            pos = next_pos
    return seen

def find_guard_start(grid):
    guard_start_idx = grid._data.index('^')
    guard_start = (guard_start_idx % grid._x_size, guard_start_idx // grid._y_size)
    return guard_start

def part1(input):
    grid = make_grid(input)
    guard_start = find_guard_start(grid)
    return find_guard_positions(grid, guard_start, up)

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

def part2(input):
    grid = make_grid(input)
    guard_start = find_guard_start(grid)
    visited_locations = find_guard_positions(grid, guard_start, up)
    loop_pos = set()
    for visited_location in visited_locations:
        if is_loop(grid, guard_start, up, visited_location):
            loop_pos.add(visited_location)
    return len(loop_pos)

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = len(part1(example))
        assert ans == 41
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = len(part1(input))
        assert ans == 4789
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = part2(example)
        assert ans == 6
        print(f'Pt2_v2(example)::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans == 1304, f"Expected: {1304}, got: {ans}"
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
