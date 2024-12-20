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
def get_nbrs(grid, pos):
    px, py = pos
    return [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']


def path_cost(grid, end, Q, seen):
    while Q:
        cost, pos = heappop(Q)
        if pos == end:
            return cost

        if pos in seen:
            continue
        seen.add(pos)

        nbrs = get_nbrs(grid, pos)

        for n in nbrs:
            heappush(Q, (cost + 1, n))


def deploy_cheaters(grid, start, end):
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

        legit_nbrs = get_nbrs(grid, pos)
        for n in legit_nbrs:
            heappush(Q, (cost + 1, n))

        px, py = pos
        cheat_nbrs = []
        for n in [n for n in grid.get_nbr_xys(px, py) if grid.at(n) == '#']:
            direction = sub(n, pos)
            p_after_cheat = add(pos, mul(direction, 2))
            if grid.in_bounds(p_after_cheat) and grid.at(p_after_cheat) != '#' and p_after_cheat not in seen:
                cheat_id = (pos, p_after_cheat)
                if cheat_id not in seen_cheats:
                    # n is in the wall, but the algo should take care of it fine and get the cost right
                    cheat_nbrs.append(n)
                    seen_cheats.add(cheat_id)

        for cn in cheat_nbrs:
            forked_Q = deepcopy(Q)
            heappush(forked_Q, (cost + 1, cn))
            seen_copy = deepcopy(seen)
            found_costs.append(path_cost(grid, end, forked_Q, seen_copy))



def part1(input, is_example=False):
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

    found_costs = deploy_cheaters(grid, start, end)
    savings_freq = {}
    for cost in found_costs:
        savings_freq.setdefault(no_cheat_cost - cost, 0)
        savings_freq[no_cheat_cost - cost] += 1

    if is_example:
        print(savings_freq)
        expected = {4: 14, 2: 14, 12: 3, 10: 2, 8: 4, 6: 2, 64: 1, 40: 1, 38: 1, 20: 1, 36: 1, 0: 1}
        assert savings_freq == expected, f'Err, got: {savings_freq}'

    return sum(savings_freq[k] for k in savings_freq.keys() if k >= 100)


def part2(input):
    answer = '...'
    return answer


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example, True)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 1518, "Got: {}".format(ans)
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
