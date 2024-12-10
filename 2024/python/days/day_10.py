from pathlib import Path

from utils.grid import make_grid
from utils.misc import timer
from utils.read import read_input

example = """89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732"""

small_example = """
0123
1234
8765
9876""".strip()

def part1(input):
    grid = make_grid(input, as_ints=True)
    trailheads = []
    for y in range(grid._y_size):
        for x in range(grid._x_size):
            if grid.at(x, y) == 0:
                trailheads.append((x, y))

    point_to_tops_reached = {}
    def iter(point):
        if point in point_to_tops_reached:
            return point_to_tops_reached[point]

        val = grid.at_p(point)
        if val == 9:
            return set([point])
        nbr_xys = grid.get_nbr_xys(point[0], point[1])
        tops_reached = set()
        for nbr_point in nbr_xys:
            if grid.at_p(nbr_point) == val + 1:
                tops_reached = tops_reached.union(iter(nbr_point))
        point_to_tops_reached[point] = tops_reached
        return tops_reached

    answer = 0
    for trailhead in trailheads:
        answer += len(iter(trailhead))

    return answer

def part2(input):
    grid = make_grid(input, as_ints=True)
    trailheads = []
    for y in range(grid._y_size):
        for x in range(grid._x_size):
            if grid.at(x, y) == 0:
                trailheads.append((x, y))

    point_to_num_routes = {}
    def iter(point):
        if point in point_to_num_routes:
            return point_to_num_routes[point]

        val = grid.at_p(point)
        if val == 9:
            return 1
        nbr_xys = grid.get_nbr_xys(point[0], point[1])
        num_routes = 0
        for nbr_point in nbr_xys:
            if grid.at_p(nbr_point) == val + 1:
                num_routes += iter(nbr_point)
        point_to_num_routes[point] = num_routes
        return num_routes

    answer = 0
    for trailhead in trailheads:
        answer += iter(trailhead)

    return answer

def maxs_solution(input):
    grid = {i + j * 1j: int(c) for i, r in enumerate(input.splitlines()) for j, c in enumerate(r.strip())}
    paths = [[p] for p in grid if grid[p] == 0]
    for i in range(1, 10):
        paths = [p + [n] for d in [1, -1j, -1, 1j] for p in paths if ((n := p[-1] + d) in grid and grid[n] == i)]
    return paths


def part2_max(input):
    return len((maxs_solution(input)))

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(small_example)
        assert ans == 1, "Got: {}".format(ans)
        print(f'Pt1(small_example)::ans: {ans}')

    with timer():
        ans = part1(example)
        assert ans == 36, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 482, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = part2(example)
        assert ans == 81, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')


    with timer():
        ans = part2_max(input)
        assert ans == 1094, "Got: {}".format(ans)
        print(f'Pt2(Max)::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans == 1094, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')

if __name__ == "__main__":
    run()