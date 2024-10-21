from pathlib import Path

from utils.misc import timer, partition
from utils.read import read_input

context = """
  4     5  5
  9     0  0
  4     0  3
0 ......+...
1 ..........
2 ..........
3 ..........
4 ....#...##
5 ....#...#.
6 ..###...#.
7 ........#.
8 ........#.
9 #########."""

example = """498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9"""

"498,4 -> 498,6 -> 496,6".split(' -> ')

sand_source_xy = (500,0)


def build_rock_xys(input):
    rock_xys = set()
    for line in input.splitlines():
        segments = partition(2, 1, [list(map(int, xy.split(','))) for xy in line.split(' -> ')])
        for segment in segments:

            start_x, start_y = segment[0]
            end_x, end_y = segment[1]

            if start_x == end_x:
                rock_xys = rock_xys.union({(start_x, y) for y in range(min(start_y, end_y), max(start_y, end_y) + 1)})
            elif start_y == end_y:
                rock_xys = rock_xys.union({(x, start_y) for x in range(min(start_x, end_x), max(start_x, end_x) + 1)})
            else:
                raise Exception(f"Segments aren't on axis aligned line {segment}")

    return rock_xys

def down(xy):
    return xy[0], xy[1] + 1

def below_left(xy):
    return xy[0] - 1, xy[1] + 1

def below_right(xy):
    return xy[0] + 1, xy[1] + 1


def part1(input):
    sand_at_rest_xys = set()
    rock_xys = build_rock_xys(input)
    # print(rock_xys)
    max_rock_y = max(map(lambda xy: xy[1], rock_xys))

    max_iterations = 100000
    num_iterations = 0
    current_sand_xy = sand_source_xy
    while num_iterations < max_iterations:
        num_iterations += 1
        if current_sand_xy[1] > max_rock_y:
            # Sand now falls for infinity
            break

        one_down_xy = down(current_sand_xy)
        if one_down_xy not in rock_xys and one_down_xy not in sand_at_rest_xys:
            # Must be air, continue
            current_sand_xy = one_down_xy
            continue

        # below left is blocked
        if below_left(current_sand_xy) in sand_at_rest_xys or below_left(current_sand_xy) in rock_xys:
            # below right is blocked
            if below_right(current_sand_xy) in sand_at_rest_xys or below_right(current_sand_xy) in rock_xys:
                sand_at_rest_xys.add(current_sand_xy)
                current_sand_xy = sand_source_xy
                continue
            else:
                current_sand_xy = below_right(current_sand_xy)
                continue
        else:
            current_sand_xy = below_left(current_sand_xy)
            continue

    if num_iterations == max_iterations:
        raise Exception(f"Num iterations reached max iterations {max_iterations}")

    answer = len(sand_at_rest_xys)
    print(f'Pt1::ans: {answer}')

def part2(input):
    sand_at_rest_xys = set()
    rock_xys = build_rock_xys(input)
    # print(rock_xys)
    max_rock_y = max(map(lambda xy: xy[1], rock_xys))

    def in_rock_xys(xy):
        return xy in rock_xys or xy[1] == max_rock_y + 2

    max_iterations = int(1e7)
    num_iterations = 0
    current_sand_xy = sand_source_xy
    while num_iterations < max_iterations:
        num_iterations += 1

        one_down_xy = down(current_sand_xy)
        if not in_rock_xys(one_down_xy) and one_down_xy not in sand_at_rest_xys:
            # Must be air, continue
            current_sand_xy = one_down_xy
            continue

        # below left is blocked
        if below_left(current_sand_xy) in sand_at_rest_xys or in_rock_xys(below_left(current_sand_xy)):
            # below right is blocked
            if below_right(current_sand_xy) in sand_at_rest_xys or in_rock_xys(below_right(current_sand_xy)):
                sand_at_rest_xys.add(current_sand_xy)
                if current_sand_xy == sand_source_xy:
                    break
                else:
                    current_sand_xy = sand_source_xy
                    continue
            else:
                current_sand_xy = below_right(current_sand_xy)
                continue
        else:
            current_sand_xy = below_left(current_sand_xy)
            continue

    if num_iterations == max_iterations:
        raise Exception(f"Num iterations reached max iterations {max_iterations}")

    answer = len(sand_at_rest_xys)
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0]
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