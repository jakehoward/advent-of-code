from pathlib import Path
import random
from statistics import mean, stdev

from utils.grid import Grid
from utils.misc import timer
from utils.read import read_input

example = """p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3"""


def parse(input):
    robots = []
    for line in input.splitlines():
        p, v = line.split(' ')
        px, py = p.split('=')[1].split(',')
        vx, vy = v.split('=')[1].split(',')
        robots.append(((int(px), int(py)), (int(vx), int(vy))))
    return robots

def position_after(robot, iters, x_size, y_size):
    (px, py), (vx, vy) = robot
    return (px + iters * vx) % x_size, (py + iters * vy) % y_size

def calc_safety_factor(robots, x_size, y_size):
    final_positions = [position_after(robot, 100, x_size, y_size) for robot in robots]
    top_left, top_right, bottom_left, bottom_right = 0, 0, 0, 0
    for px, py in final_positions:
        if px < x_size // 2:
            if py < y_size // 2:
                top_left += 1
            elif py > (y_size // 2):
                bottom_left += 1
        elif px > (x_size // 2):
            if py < y_size // 2:
                top_right += 1
            elif py > (y_size // 2):
                bottom_right += 1
    return top_left * top_right * bottom_left * bottom_right


def part1(input, x_size,  y_size):
    robots = parse(input)
    answer = calc_safety_factor(robots, x_size, y_size)
    return answer


def xmas_tree_stepper(start_robots, grid, x_size, y_size):
    robots = start_robots
    iters = 0
    while True:
        robots = [(position_after(robot, iters, x_size, y_size), robot[1]) for robot in robots]
        # overlays = [(pos, 'X') for pos, vel in robots]
        # print('Num iters:', iters)
        # print(grid.as_string(overlays, True))
        yield iters, robots
        iters += 1

# def is_xmas_tree(xys):
#     ds = [(-1, 0), (-1, -1), (0, -1), (1, -1), (1, 0), (1, 1), (0, 1), (-1, 1)]
#     positions = set(xys)
#
#     for attempt in list(set([random.choice(xys) for _ in range(10)])):
#         target = len(positions) // 50
#         pos = attempt
#         seen = set()
#         while len(seen) <= target:
#             if len(seen) == target:
#                 return True
#             if pos not in positions:
#                 break
#             seen.add(pos)
#             px, py = pos
#             nbrs = [(px + dx, py + dy) for dx, dy in ds if (px + dx, py + dy) not in seen]
#             if not nbrs:
#                 break
#             pos = nbrs[0]
#     return False

def straight_lines_2x2(xys, x_size, y_size):
    positions = set(xys)
    ds = [(0, 0), (1, 0), (0, 1), (1, 1)]
    num_straight_lines = 0
    for y in range(y_size):
        for x in range(x_size):
            if sum([1 for dx, dy in ds if (x + dx, y + dy) in positions]) == 2:
                num_straight_lines += 1
    return num_straight_lines

def empty_nXn(xys, x_size, y_size, n):
    positions = set(xys)
    ds = [(0 + nx, 0 + ny) for nx in range(n) for ny in range(n)]
    empty_count = 0
    for y in range(0, y_size, n):
        for x in range(0, x_size, n):
            if all([(x + dx, y + dy) not in positions for dx, dy in ds]):
                empty_count += 1
    return empty_count


def part2(input):
    start_robots = parse(input)
    x_size = 101
    y_size = 103
    data = ['.'] * 101 * 103
    grid = Grid(data, x_size, y_size)
    robot_stepper = xmas_tree_stepper(start_robots, grid, x_size, y_size)
    max_iters = 100_000
    iter_empty_count_pairs = []
    while True:
        iters, robots = next(robot_stepper)
        empty_count = empty_nXn([pxy for pxy, v in robots], x_size, y_size, 4)
        iter_empty_count_pairs.append((iters, empty_count))
        if empty_count == 352:
            overlays = [(pos, 'X') for pos, vel in robots]
            print(f'iters: {iters}')
            print(grid.as_string(overlays, True))
            break
        if iters % 10000 == 0:
            print(f'iters: {iters}')
        if iters > max_iters:
            break

    empty_counts = [line_count for iter, line_count in iter_empty_count_pairs]
    print('mean:', mean(empty_counts), 'min:', min(empty_counts), 'max:', max(empty_counts), 'std:', stdev(empty_counts))


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example, 11, 7)
        assert ans == 12, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input, 101, 103)
    #     assert ans == None, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')

    with timer():
        part2(input)
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
