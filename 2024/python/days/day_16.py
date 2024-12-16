from pathlib import Path
from heapq import heappush, heappop
import math

from utils.grid import make_grid
from utils.misc import timer
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


# https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
# (Indentation mine)
#  1 function Dijkstra(Graph, source):
#  2
#  3 for each vertex v in Graph.Vertices:
#  4    dist[v] ← INFINITY
#  5    prev[v] ← UNDEFINED
#  6    add v to Q
#  7    dist[source] ← 0
#  8
#  9 while Q is not empty:
# 10    u ← vertex in Q with minimum dist[u]
# 11    remove u from Q
# 12
# 13    for each neighbor v of u still in Q:
# 14        alt ← dist[u] + Graph.Edges(u, v)
# 15        if alt < dist[v]:
# 16        dist[v] ← alt
# 17        prev[v] ← u
# 18        add v to Q with distance alt
# 19
# 20 return dist[], prev[]

def dijkstra(grid, start, end):
    Q = []
    start_to_vertex_cost = {}
    vertex_to_prev_vertices = {}
    for point, _ in grid:
        if grid.at(point) != '#':
            for direction in [up, down, left, right]:
                vertex = (point, direction)
                start_to_vertex_cost[vertex] = math.inf
                vertex_to_prev_vertices[vertex] = []
                heappush(Q, (start_to_vertex_cost[vertex], vertex))

    start_to_vertex_cost[(start, right)] = 0
    heappush(Q, (0, (start, right)))

    while Q:
        cost, vertex = heappop(Q)

        if cost > start_to_vertex_cost[(end, up)] or cost > start_to_vertex_cost[(end, down)] or cost > \
                start_to_vertex_cost[(end, left)] or cost > start_to_vertex_cost[(end, right)]:
            continue
        point, direction = vertex

        px, py = point
        nbrs = []
        if direction == up:
            if grid.at((px, py - 1)) != '#':
                nbrs.append((cost + 1, ((px, py - 1), direction)))
            nbrs.append((cost + 1000, (point, left)))
            nbrs.append((cost + 1000, (point, right)))
        if direction == down:
            if grid.at((px, py + 1)) != '#':
                nbrs.append((cost + 1, ((px, py + 1), direction)))
            nbrs.append((cost + 1000, (point, left)))
            nbrs.append((cost + 1000, (point, right)))
        if direction == left:
            if grid.at((px - 1, py)) != '#':
                nbrs.append((cost + 1, ((px - 1, py), direction)))
            nbrs.append((cost + 1000, (point, up)))
            nbrs.append((cost + 1000, (point, down)))
        if direction == right:
            if grid.at((px + 1, py)) != '#':
                nbrs.append((cost + 1, ((px + 1, py), direction)))
            nbrs.append((cost + 1000, (point, up)))
            nbrs.append((cost + 1000, (point, down)))
        for nbr_cost, nbr_vertex in nbrs:
            if nbr_cost == start_to_vertex_cost[nbr_vertex]:
                vertex_to_prev_vertices[nbr_vertex].append(vertex)
                heappush(Q, (nbr_cost, nbr_vertex))
            elif nbr_cost < start_to_vertex_cost[nbr_vertex]:
                start_to_vertex_cost[nbr_vertex] = nbr_cost
                vertex_to_prev_vertices[nbr_vertex] = [vertex]
                heappush(Q, (nbr_cost, nbr_vertex))
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
    start_to_pos_cost, pos_to_prev_pos = dijkstra(grid, start, end)
    all_points = set()
    for end in [pos for pos in pos_to_prev_pos.keys() if pos[0] == end]:
        print(end)
        points = set(dfs(pos_to_prev_pos, end, (start, right), set()))
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
        #     assert ans == None, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
