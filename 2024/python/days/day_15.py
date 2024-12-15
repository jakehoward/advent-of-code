from pathlib import Path
from collections import deque

from utils.grid import make_grid, make_grid_with_points
from utils.misc import timer
from utils.read import read_input

smaller_example = """
#######
#...#.#
#.....#
#..OO@#
#..O..#
#.....#
#######

<vv<<^^<<^^
""".strip()

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

up = (0, -1)
down = (0, 1)
left = (-1, 0)
right = (1, 0)

dir_map = {'^': up, 'v': down, '<': left, '>': right}


def parse(input):
    grid, dirs = input.split('\n\n')
    grid = make_grid(grid)
    return grid, deque([d for l in dirs.splitlines() for d in l])


def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]


def sub(p1, p2):
    return p1[0] - p2[0], p1[1] - p2[1]


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


def print_grid(robot_xy, walls, boxes, x_size, y_size):
    overlays = [(add(p, right), ']') for p in boxes]
    print(make_grid_with_points({'#': walls, '[': boxes, '@': {robot_xy}}, x_size, y_size,
                                default='.').as_string(overlays))


def part2(input, DEBUG=False):
    grid, dirs = parse(input)
    start = None
    boxes = set()
    walls = set()
    x_size = grid._x_size * 2
    y_size = grid._y_size
    for (px, py), v in grid:
        if v == '@':
            start = (px * 2, py)
        elif v == '#':
            walls.add((px * 2, py))
            walls.add((px * 2 + 1, py))
        elif v == 'O':
            boxes.add((px * 2, py))
    assert start is not None, 'Could not find start'

    if DEBUG: print_grid(start, walls, boxes, x_size, y_size)

    pos = start
    while dirs:
        mv_str = dirs.popleft()
        if DEBUG: print('Move', mv_str + ':')
        d = dir_map[mv_str]

        boxes_to_move = []
        if d == right:
            next_pos = add(pos, d)
            while next_pos in boxes:
                boxes_to_move.append(next_pos)
                next_pos = add(next_pos, add(d, d))
        elif d == left:
            next_pos = add(pos, add(d, d))
            while next_pos in boxes:
                boxes_to_move.append(next_pos)
                next_pos = add(next_pos, add(d, d))
        elif d == up or d == down:
            next_pos = add(pos, d)
            if next_pos in boxes:
                boxes_to_move.append(next_pos)
            elif add(next_pos, left) in boxes:
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
                        if (bx - 1, ny) in boxes:
                            extra_boxes_to_move.append((bx - 1, ny))
                            found_box = True
                        if (bx, ny) in boxes:
                            extra_boxes_to_move.append((bx, ny))
                            found_box = True
                        if (bx + 1, ny) in boxes:
                            extra_boxes_to_move.append((bx + 1, ny))
                            found_box = True
                        extra_boxes_to_move = list(set(extra_boxes_to_move))
                boxes_to_move += extra_boxes_to_move
                iters += 1
                if not found_box:
                    break

        blocked = False
        for box in boxes_to_move:
            if add(box, d) in walls or add(add(box, right), d) in walls:
                blocked = True
                break
        if add(pos, d) in walls: blocked = True
        if blocked:
            if DEBUG:
                print('Blocked')
                print_grid(pos, walls, boxes, x_size, y_size)
            continue

        for box in boxes_to_move:
            boxes.remove(box)
        for box in boxes_to_move:
            boxes.add(add(box, d))

        pos = add(pos, d)
        if DEBUG: print_grid(pos, walls, boxes, x_size, y_size)

    if DEBUG:
        print('Final state:')
        print_grid(pos, walls, boxes, x_size, y_size)
    ans = 0
    for box in boxes:
        ans += 100 * box[1] + box[0]
    return ans


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
    #     ans = part2(smaller_example)
    #     print(f'Pt2(smaller_example)::ans: {ans}')
    #     ans = None

    with timer():
        ans = part2(example)
        assert ans == 9021, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part2(input, False)
        assert ans == 1458740, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
