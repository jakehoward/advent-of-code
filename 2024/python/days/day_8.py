from pathlib import Path

from utils.grid import make_grid
from utils.misc import timer
from utils.read import read_input

example = """............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............"""

example_2 = """T....#....
...T......
.T....#...
.........#
..#.......
..........
...#......
..........
....#.....
.........."""

def get_antenna_locs_by_type(grid):
    antenna_locs_by_type = {}
    for x in range(grid._x_size):
        for y in range(grid._y_size):
            node = grid.at(x, y)
            if node != '.' and node != '#':
                antenna_locs_by_type.setdefault(node, []).append((x, y))
    return antenna_locs_by_type


def part1(input):
    grid = make_grid(input)
    antenna_locs_by_type = get_antenna_locs_by_type(grid)

    antinodes = set()
    for name, antenna_locs in antenna_locs_by_type.items():
        for i, antenna_a in enumerate(antenna_locs):
            for j, antenna_b in enumerate(antenna_locs):
                if i != j:
                    dx = antenna_a[0] - antenna_b[0]
                    dy = antenna_a[1] - antenna_b[1]
                    anti_1 = (antenna_a[0] + dx, antenna_a[1] + dy)
                    anti_2 = (antenna_b[0] - dx, antenna_b[1] - dy)
                    if grid.in_bounds_p(anti_1):
                        antinodes.add(anti_1)
                    if grid.in_bounds_p(anti_2):
                        antinodes.add(anti_2)

    return len(antinodes)


def part2(input):
    grid = make_grid(input)
    antenna_locs_by_type = get_antenna_locs_by_type(grid)
    antinodes = set()
    for name, antenna_locs in antenna_locs_by_type.items():
        for i, antenna_a in enumerate(antenna_locs):
            for j, antenna_b in enumerate(antenna_locs):
                antinodes.add(antenna_a)
                antinodes.add(antenna_b)
                if i != j:
                    dx = antenna_a[0] - antenna_b[0]
                    dy = antenna_a[1] - antenna_b[1]
                    anti_1 = (antenna_a[0] + dx, antenna_a[1] + dy)
                    anti_2 = (antenna_b[0] - dx, antenna_b[1] - dy)
                    while grid.in_bounds_p(anti_1) or grid.in_bounds_p(anti_2):
                        if grid.in_bounds_p(anti_1):
                            antinodes.add(anti_1)
                        if grid.in_bounds_p(anti_2):
                            antinodes.add(anti_2)
                        anti_1 = (anti_1[0] + dx, anti_1[1] + dy)
                        anti_2 = (anti_2[0] - dx, anti_2[1] - dy)
    return len(antinodes)

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 14
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 247
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = part2(example_2)
        assert ans == 9, "Expected answer is 9, got: {}".format(ans)
        print(f'Pt2_v2(example)::ans: {ans}')

    with timer():
        ans = part2(example)
        assert ans == 34, "Expected answer is 34, got: {}".format(ans)
        print(f'Pt2_v2(example)::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans == 861, "Expected answer is 861, got: {}".format(ans)
        print(f'Pt2::ans: {ans}')

# todo:
# - Make iterating the grid coords a member of grid
# - Don't check pairs twice (itertools)
# - Add printing grid (with overlay) as member of grid

if __name__ == "__main__":
    run()