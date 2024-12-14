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


def nothing_in_top_wedges(robots, x_size, y_size):
    positions = set([pos for pos, _ in robots])
    height = y_size - (y_size // 20)
    top_size = x_size - (x_size // 3)
    x_step = top_size // height
    for y in range(height):
        left_xys = [(x, y) for x in range(top_size - y * x_step)]
        right_xys = [(x, y) for x in range((x_size // 2) + 1, (x_size // 2) + 1 + top_size + - y * x_step, 1)]
        if any([p in positions for p in left_xys]) or any([p in positions for p in right_xys]):
            return False
    return True


def find_wedges_population(robots, x_size):
    positions = set([pos for pos, _ in robots])
    top_size = (x_size - (x_size // 4)) // 2
    x_step = 1
    height = top_size
    num_robots = 0
    for y in range(height):
        left_xys = [(x, y) for x in range(top_size - y * x_step)]
        right_end = x_size
        right_start = right_end - top_size - y * x_step
        right_xys = [(x, y) for x in range(right_start, right_end, 1)]
        num_robots += sum([1 for p in left_xys + right_xys if p in positions])
    return num_robots

def find_bottom_population(robots, x_size, y_size):
    positions = [pos for pos, _ in robots]
    top_left, top_right, bottom_left, bottom_right = 0, 0, 0, 0
    for px, py in positions:
        if px < (x_size // 2) - 10:
            # if py < (y_size // 2):
            #     top_left += 1
            if py > y_size - 10:#(y_size // 2):
                bottom_left += 1
        elif px > (x_size // 2) + 10:
            # if py < y_size // 2:
            #     top_right += 1
            if py > y_size - 10: #(y_size // 2):
                bottom_right += 1
    return bottom_left * bottom_right


def find_population(robots, x_size, y_size):
    positions = [pos for pos, _ in robots]
    top, bottom = 0, 0
    for px, py in positions:
        if py < (y_size // 2):
            bottom += 1
        else:
            top += 1
    return bottom - top



def find_num_contiguous(robots):
    robot_pos = set([p for p, _ in robots])
    seen = set()
    num_contiguous = 0
    while robot_pos:
        pos = robot_pos.pop()
        if pos in seen:
            continue
        num_contiguous += 1

        # Flood fill from the position
        ds = [(-1, 1), (0, 1), (1, 1),
              (-1, 0), (1, 0),
              (-1, -1), (0, -1), (1, -1)]
        px, py = pos
        nbrs = set([(px + dx, py + dy) for dx, dy in ds if (px + dx, py + dy) in robot_pos])
        seen_nbrs = set()
        while nbrs:
            nbr = nbrs.pop()
            if nbr in seen_nbrs:
                continue
            seen_nbrs.add(nbr)
            nx, ny = nbr
            nbrs = nbrs.union([(nx + dx, ny + dy) for dx, dy in ds if (nx + dx, ny + dy) in robot_pos])

        seen.add(pos)
        seen = seen.union(nbrs)
    return num_contiguous

def find_point_spacing_std_dev(robots, y_size):
    robot_xs_by_row = {y: sorted([px for (px, py), _ in robots if py == y]) for y in range(y_size)}
    x_spaces = [abs(x2 - x1) for xs in robot_xs_by_row.values() for x1, x2 in zip(xs, xs[1:])]
    return stdev(x_spaces)


def part2(input):
    start_robots = parse(input)
    x_size = 101
    y_size = 103
    grid = Grid(['.'] * (101 * 103), x_size, y_size)
    cycle_sizes = [find_cycle_size(robot, x_size, y_size) for robot in start_robots]
    cycle_size = -1
    if len(set(cycle_sizes)) == 1:
        cycle_size = cycle_sizes[0]
    else:
        raise Exception("Can't find cycle size")
    print('Cycle size:', cycle_size)
    step = robot_stepper(start_robots, x_size, y_size)
    # stat_under_inspection = []
    iter_population = []
    for _ in range(cycle_size + 1):
        iter, robots = next(step)
        population = find_population(robots, x_size, y_size)
        iter_population.append((iter, population))
        # wedge_population = find_wedges_population(robots, x_size, y_size)
        # num_contig_shapes = find_num_contiguous(robots)
        # std_dev_x_spacing = find_point_spacing_std_dev(robots, y_size)
        # stat_under_inspection.append(std_dev_x_spacing)
        # if iter % 1000 == 0:
        #     print('...iter:', iter)
        # if num_contig_shapes < 90:
        # if std_dev_x_spacing < 13:
        #     overlays = [(pos, 'X') for pos, v in robots]
        #     print('Iters:', iter)
        #     print(grid.as_string(overlays, True))
        #     return iter
    for iter, pop in sorted(iter_population, key=lambda x: x[1], reverse=True)[:500]:
        overlays = [(pos, 'X') for pos in [position_after(robot, iter, x_size, y_size) for robot in start_robots]]
        print('Iters:', iter, 'Population/stat:', pop)
        print(grid.as_string(overlays, True))

    return -1
    # print('mean:', mean(stat_under_inspection), 'min:', min(stat_under_inspection), 'max:', max(stat_under_inspection), 'std:', stdev(stat_under_inspection))


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
