from pathlib import Path
from heapq import heappush, heappop
import math

from utils.grid import make_grid
from utils.misc import timer
from utils.path_finding import solve_graph_for_single_node
from utils.read import read_input

example = """
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############""".strip()

example_2 = """
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################""".strip()

up = 'U'
down = 'D'
left = 'L'
right = 'R'


def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]


def get_direction(frm, to):
    if add(frm, (0, -1)) == to:
        return up
    if add(frm, (0, 1)) == to:
        return down
    if add(frm, (-1, 0)) == to:
        return left
    if add(frm, (1, 0)) == to:
        return right
    raise Exception("You're directionless, sort your life out (╯°□°)╯︵ ┻━┻")


def cost_to_move(current_pos, current_direction, next_pos):
    new_direction = get_direction(current_pos, next_pos)
    if current_direction == new_direction:
        return 1
    if (current_direction == up and new_direction == down) or \
            (current_direction == down and new_direction == up) or \
            (current_direction == left and new_direction == right) or \
            (current_direction == right and new_direction == left):
        return 1 + 2 * 1000
    return 1001


def shortest_path(grid, start, end):
    seen = set()
    q = []
    heappush(q, (0, (start, right)))
    while q:
        cost, (p, facing) = heappop(q)
        if p == end:
            return cost
        if p in seen:
            continue
        seen.add(p)

        px, py = p
        nbrs = [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#' and n not in seen]
        for n in nbrs:
            nbr_cost = cost_to_move(p, facing, n)
            nbr_direction = get_direction(p, n)
            heappush(q, (cost + nbr_cost, (n, nbr_direction)))


def part1(input):
    grid = make_grid(input)
    start, end = None, None
    for p, v in grid:
        if v == 'S':
            start = p
        if v == 'E':
            end = p
    answer = shortest_path(grid, start, end)
    return answer


def dijkstra(grid, start):
    def get_all_vertices(grid):
        vertices = []
        for point, _ in grid:
            if grid.at(point) != '#':
                for direction in [up, down, left, right]:
                    vertex = (point, direction)
                    vertices.append(vertex)
        return vertices

    def get_nbrs(grid, vertex):
        point, _ = vertex
        px, py = point
        return [(n, get_direction(point, n)) for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']

    def get_cost(from_vertex, to_vertex):
        return cost_to_move(from_vertex[0], from_vertex[1], to_vertex[0])

    start_to_vertex_cost, vertex_to_prev_vertices = solve_graph_for_single_node(grid, get_all_vertices, get_nbrs, get_cost, (start, right))

    return start_to_vertex_cost, vertex_to_prev_vertices


def dfs(pos_to_prev_pos, from_point, to_point, seen):
    if from_point == to_point:
        seen.add(to_point)
        return
    for opt in pos_to_prev_pos[from_point]:
        if opt in seen:
            continue
        seen.add(opt)
        dfs(pos_to_prev_pos, opt, to_point, seen)
    return [p for p, _ in seen]


def part2(input):
    grid = make_grid(input)
    start, end = None, None
    for p, v in grid:
        if v == 'S':
            start = p
        if v == 'E':
            end = p
    print('Start D')
    start_to_pos_cost, pos_to_prev_pos = dijkstra(grid, start)
    print('End D')
    all_points = set()
    end_vertices = [pos for pos in pos_to_prev_pos.keys() if pos[0] == end]
    min_cost = min([start_to_pos_cost[e] for e in end_vertices])
    for end in [e for e in end_vertices if start_to_pos_cost[e] == min_cost]:
        points = set(dfs(pos_to_prev_pos, end, (start, right), {end}))
        all_points = all_points.union(points)
    return len(set(all_points))


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        for ex, expected in [(example, 7036), (example_2, 11048)]:
            ans = part1(ex)
            assert ans == expected, "Expected: {}, Got: {}".format(expected, ans)
            print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 103512, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    with timer():
        for ex, expected in [(example, 45), (example_2, 64)]:
            ans = part2(ex)
            assert ans == expected, "Expected: {}, Got: {}".format(expected, ans)
            print(f'Pt2(example)::ans: {ans}')
            ans = None

    with timer():
        ans = part2(input)
    #     #     assert ans == None, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
