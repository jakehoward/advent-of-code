from pathlib import Path
from collections import deque

from utils.grid import make_grid, make_grid_with_points
from utils.misc import timer
from utils.read import read_input
small_example = """
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<""".strip()

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

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^""".strip()

dir_map = {'^': (0, -1), 'v': (0, 1), '<': (-1, 0), '>': (1, 0)}

def parse(input):
    grid, dirs = input.split('\n\n')
    grid = make_grid(grid)
    return grid, deque([d for l in dirs.splitlines() for d in l])

def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]

def part1(input):
    grid, dirs = parse(input)
    start = None
    boxes = set()
    walls = set()
    for p, v in grid:
        if v == '@':
            start = p
        elif v == '#':
            walls.add(p)
        elif v == 'O':
            boxes.add(p)
    assert start is not None, 'Could not find start'

    pos = start
    while dirs:
        d = dir_map[dirs.popleft()]

        boxes_to_move = []
        next_pos = add(pos, d)
        while next_pos in boxes:
            boxes_to_move.append(next_pos)
            next_pos = add(next_pos, d)

        if grid.at(next_pos) == '#':
            continue

        for box in boxes_to_move:
            boxes.remove(box)
        for box in boxes_to_move:
            boxes.add(add(box, d))

        pos = add(pos, d)

    ans = 0
    for box in boxes:
        ans += 100 * box[1] + box[0]

    # print(make_grid_with_points({'#': walls, 'O': boxes, '@': {pos}}, grid._x_size, grid._y_size,
    #                             default='.').as_string())
    return ans


def part2(input):
    answer = '...'
    return answer


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)

    with timer():
        ans = part1(small_example)
        assert ans == 2028, "Got: {}".format(ans)
        print(f'Pt1(small_example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(example)
        assert ans == 10092, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 1430439, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    # with timer():
    #     ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2::ans: {ans}')
    #     ans = None


if __name__ == "__main__":
    run()
