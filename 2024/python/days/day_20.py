from copy import deepcopy
from functools import cache
from heapq import heappush, heappop
from pathlib import Path

from utils.grid import make_grid
from utils.misc import timer
from utils.read import read_input

example = """
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############""".strip()


def sub(p1, p2):
    return p1[0] - p2[0], p1[1] - p2[1]


def mul(p, s):
    return p[0] * s, p[1] * s


def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]


@cache
def shortest_path_cost(grid, start, end):
    Q = []
    seen = set()

    heappush(Q, (0, start))
    while Q:
        cost, pos = heappop(Q)
        if pos == end:
            return cost

        if pos in seen:
            continue
        seen.add(pos)

        px, py = pos
        nbrs = [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']

        for n in nbrs:
            heappush(Q, (cost + 1, n))



def get_cheat_nbrs_within_distance(grid, pos, distance):
    px, py = pos
    nbrs = set([n for n in grid.get_nbr_xys(px, py)])

    for d in range(distance - 1):
        new_nbrs = set()
        for nx, ny in nbrs:
            for nn in grid.get_nbr_xys(nx, ny):
                new_nbrs.add(nn)
        nbrs = nbrs.union(new_nbrs)

    return [n for n in nbrs if grid.at(n) != '#']


def path_cost(grid, end, Q, seen):
    while Q:
        cost, pos = heappop(Q)
        if pos == end:
            return cost

        if pos in seen:
            continue
        seen.add(pos)

        px, py = pos
        nbrs = [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']

        for n in nbrs:
            heappush(Q, (cost + 1, n))


def deploy_cheaters(grid, start, end, max_cheat_distance):
    Q = []
    seen = set()
    heappush(Q, (0, start))
    seen_cheats = set()

    found_costs = []
    while Q:
        cost, pos = heappop(Q)
        if pos == end:
            found_costs.append(cost)
            return found_costs

        if pos in seen:
            continue
        seen.add(pos)

        px, py = pos
        legit_nbrs = [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']

        for n in legit_nbrs:
            heappush(Q, (cost + 1, n))

        cheat_nbrs = []
        for n in get_cheat_nbrs_within_distance(grid, pos, max_cheat_distance):
            if n not in seen:
                cheat_id = (pos, n)
                if cheat_id not in seen_cheats:
                    cheat_nbrs.append(n)
                    seen_cheats.add(cheat_id)

        px, py = pos
        for cn in cheat_nbrs:
            cnx, cny = cn
            distance = abs(cnx - px) + abs(cny - py)
            # forked_Q = Q[:]  # deepcopy soooo slooooooow
            # heappush(forked_Q, (cost + distance, cn))
            # seen_copy = {x for x in seen}  # deepcopy soooo slooooooow
            found_costs.append(cost + distance + shortest_path_cost(grid, cn, end))


def get_num_options(input, cheat_distance, min_saving, is_example=False, example_expected=None):
    grid = make_grid(input)
    end = None
    start = None
    for p, v in grid:
        if v == 'E':
            end = p
        elif v == 'S':
            start = p

    Q = []
    heappush(Q, (0, start))
    no_cheat_cost = path_cost(grid, end, Q, set())
    if is_example:
        assert no_cheat_cost == 84, f'Got: {no_cheat_cost}, expected: {84}'
    else:
        assert no_cheat_cost == 9432, f'Got: {no_cheat_cost}, expected: {9432}'
    print('No cheat cost:', no_cheat_cost)

    found_costs = deploy_cheaters(grid, start, end, cheat_distance)
    savings_freq = {}
    for cost in found_costs:
        savings_freq.setdefault(no_cheat_cost - cost, 0)
        savings_freq[no_cheat_cost - cost] += 1

    if is_example:
        print(savings_freq)
        example_expected
        if example_expected:
            assert {k: v for k, v in savings_freq.items() if k != 0} == {k: v for k, v in example_expected.items() if
                                                                         k != 0}, f'Err, got: {savings_freq}'

    return sum(savings_freq[k] for k in savings_freq.keys() if k >= min_saving)


def part1(input, is_example=False):
    ex = {4: 14, 2: 14, 12: 3, 10: 2, 8: 4, 6: 2, 64: 1, 40: 1, 38: 1, 20: 1, 36: 1, 0: 1}
    return get_num_options(input, cheat_distance=2, min_saving=0 if is_example else 100, is_example=is_example,
                           example_expected=ex)


def part2(input, is_example=False):
    return get_num_options(input, cheat_distance=20, min_saving=50 if is_example else 100, is_example=is_example)


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example, True)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part1(input)
    #     assert ans == 1518, "Got: {}".format(ans)
    #     print(f'Pt1::ans: {ans}')
    #     ans = None

    with timer():
        ans = part2(example, True)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part2(input)
        assert ans == 1032257, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
