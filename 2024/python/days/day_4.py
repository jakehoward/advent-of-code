from pathlib import Path

from utils.misc import timer
from utils.grid import make_grid
from utils.read import read_input

example = """MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX"""

def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]

def mul(p1, n):
    return p1[0] * n, p1[1] * n

add((0,1), (1,1))
mul((1,1), 3)

def part1(input):
    XMAS = ['X', 'M', 'A', 'S']
    grid = make_grid(input)
    dirs = [(0, -1),   # Up
            (0, 1),    # Down
            (-1, 0),   # Left
            (1, 0),    # Right
            (-1, -1),  # Up-Left
            (1, -1),   # Up-Right
            (-1, 1),  # Down-Left
            (1, 1)]    # Down-Right
    point = (0, 0)
    answer = 0
    while point[1] <= grid.y_max:
        for d in dirs:
            path = [point, add(point, d), add(point, mul(d, 2)), add(point, mul(d, 3))]
            letters = list(map(lambda p: grid.at(p[0], p[1]) if grid.in_bounds(p[0], p[1]) else None, path))
            if letters == XMAS:
                answer += 1
        point = (point[0] + 1 if point[0] < grid.x_max else 0, point[1] if point[0] < grid.x_max else point[1] + 1)
    print(f'Pt1::ans: {answer}')

example_2 = """
.M.S......
..A..MSMS.
.M.S.MAA..
..A.ASMSM.
.M.S.M....
..........
S.S.S.S.S.
.A.A.A.A..
M.M.M.M.M.
..........""".strip()

# mas_0 = """
# M.S
# .A.
# S.M
# """.strip()

mas_1 = """
M.S
.A.
M.S
""".strip()

mas_2 = """
M.M
.A.
S.S
""".strip()

mas_3 = """
S.M
.A.
S.M
""".strip()

# mas_4 = """
# S.M
# .A.
# M.S
# """.strip()

mas_4 = """
S.S
.A.
M.M
""".strip()

def part2(input):
    grid = make_grid(input)
    x_mases = [list(''.join(mas.split('\n'))) for mas in [mas_1, mas_2, mas_3, mas_4]]
    point = (0, 0)
    answer = 0
    while point[1] <= grid.y_max:
        sub_grid = [(0, 0), (1, 0), (2, 0),
                    (0, 1), (1, 1), (2, 1),
                    (0, 2), (1, 2), (2, 2)]
        sub_grid_points = [add(point, p) for p in sub_grid]
        sub_grid_at_point = [grid.at(p[0], p[1]) if grid.in_bounds(p[0], p[1]) else None for p in sub_grid_points]

        for mas in x_mases:
            if sub_grid_at_point[::2] == mas[::2]:
                answer += 1
                break
        point = (point[0] + 1 if point[0] < grid.x_max else 0, point[1] if point[0] < grid.x_max else point[1] + 1)
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        part1(example)

    with timer():
        part1(input)

    with timer():
        part2(example_2)

    with timer():
        part2(input)

if __name__ == "__main__":
    run()