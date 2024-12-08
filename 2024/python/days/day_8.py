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

def part1(input):
    grid = make_grid(input)
    antenna_locs_by_type = {}
    for x in range(grid._x_size):
        for y in range(grid._y_size):
            node = grid.at(x, y)
            if node != '.':
                antenna_locs_by_type.setdefault(node, []).append((x, y))

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
    answer = '...'
    return answer

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 14
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        # assert ans == None
        print(f'Pt1::ans: {ans}') # 242 -> too low

    # with timer():
    #     ans = part2(example)
    #     assert ans == None
    #     print(f'Pt2_v2(example)::ans: {ans}')

    # with timer():
    #     ans = part2(input)
    #     assert ans == None
    #     print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()