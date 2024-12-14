from pathlib import Path
import random
from statistics import mean, stdev
from heapq import heappush, nlargest, nsmallest

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


def part1(input, x_size, y_size):
    robots = parse(input)
    answer = calc_safety_factor(robots, x_size, y_size)
    return answer


def robot_stepper(start_robots, x_size, y_size):
    robots = start_robots
    iters = 0
    while True:
        robots = [(position_after(robot, iters, x_size, y_size), robot[1]) for robot in robots]
        yield iters, robots
        iters += 1


def find_cycle_size(robot, x_size, y_size):
    (start_px, start_py), (vx, vy) = robot
    max_iters = 1_000_000
    iters = 1
    while iters < max_iters:
        px = (start_px + iters * vx) % x_size
        py = (start_py + iters * vy) % y_size
        if px == start_px and py == start_py:
            return iters
        iters += 1
    return -1

def xmas_tree_mask(robots, x_size, y_size):
    positions = [p for p, _ in robots]
    # five_pct = len(positions) / 20
    top_left, top_right, bottom_left, bottom_right = 0, 0, 0, 0
    for px, py in positions:
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
    # top_similarity = 1/(abs(top_left - top_right) or 1)
    # bottom_similarity = 1/(abs(bottom_left - bottom_right) or 1)
    # bottom_top_difference = (bottom_left + bottom_right) - (top_left + top_right)
    return 1 if top_left == top_right and bottom_left == bottom_right else 0
    # return top_similarity + bottom_similarity # + bottom_top_difference
    # return bottom_top_difference

# def xmas_tree_mask(robots, x_size, y_size):
#     positions = [p for p, v in robots]
#     middle = x_size // 2 if x_size % 2 == 0 else (x_size // 2) + 1
#     def mask(pos):
#         px, py = pos
#         x_left = middle - y_factor
#         x_right = middle + y_factor
#
#
#     in_mask = sum([mask(p) for p in positions])
#     return in_mask

def xmas_tree_score(robots, iter, x_size, y_size):
    return xmas_tree_mask(robots, x_size, y_size)


def part2(input):
    start_robots = parse(input)
    x_size = 101
    y_size = 103

    grid = Grid(['.'] * (101 * 103), x_size, y_size)

    cycle_sizes = [find_cycle_size(robot, x_size, y_size) for robot in start_robots]
    cycle_size = cycle_sizes[0]
    assert cycle_size == 10403 and all([c == 10403 for c in cycle_sizes]), f"Unexpected cycle size: {cycle_size}"

    step = robot_stepper(start_robots, x_size, y_size)
    xmas_score_to_robots_heap = []
    for _ in range(cycle_size):
        iter, robots = next(step)
        heappush(xmas_score_to_robots_heap, (xmas_tree_score(robots, iter, x_size, y_size), (iter, robots)))

    for score, (iter, robots) in nlargest(25, xmas_score_to_robots_heap):
        print('Iter:', iter, 'Score:', score)
        overlays = [(p, 'O') for p, v in robots]
        print(grid.as_string(overlays, True))

    return -1

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example, 11, 7)
        assert ans == 12, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input, 101, 103)
        assert ans == 218619324, "Got: {}".format(ans)
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
