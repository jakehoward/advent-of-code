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


# Mutates seen cheats
def get_nbrs(grid, pos, cheat_taken, seen_cheats):
    px, py = pos
    nbrs = [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']
    cheat_found = False
    if not cheat_taken:
        for n in grid.get_nbr_xys(px, py):
            direction = sub(n, pos)
            p_after_cheat = add(pos, mul(direction, 2))
            if grid.at(n) == '#' and grid.in_bounds(p_after_cheat) and grid.at(p_after_cheat) != '#' and (pos, p_after_cheat) not in seen_cheats:
                nbrs.append(n) # n is in the wall, but the algo should take care of it fine and get the cost right
                seen_cheats.add((pos, p_after_cheat))
                cheat_found = True
                break
    return list(set(nbrs)), cheat_found


def path_cost(grid, start, end, seen_cheats, cheat=False):
    Q = []
    seen = set()
    heappush(Q, (0, start))
    cheat_taken = not cheat

    while Q:
        cost, pos = heappop(Q)
        if pos == end:
            return cost, seen_cheats

        if pos in seen:
            continue
        seen.add(pos)

        nbrs, cheat_found = get_nbrs(grid, pos, cheat_taken, seen_cheats)
        if cheat_found and not cheat_taken:
            cheat_taken = True

        for n in nbrs:
            heappush(Q, (cost + 1, n))


def part1(input):
    grid = make_grid(input)
    end = None
    start = None
    for p, v in grid:
        if v == 'E':
            end = p
        elif v == 'S':
            start = p

    no_cheat_cost, seen_cheats = path_cost(grid, start, end, set(), False)
    print('No cheat cost:', no_cheat_cost)

    seen_cheats = set()
    savings_freq = {}
    while True:
        len_seen_cheats_before = len(seen_cheats)
        cost, seen_cheats = path_cost(grid, start, end, seen_cheats, True)
        len_seen_cheats_after = len(seen_cheats)
        if len_seen_cheats_before == len_seen_cheats_after:
            break

        saving = no_cheat_cost - cost
        savings_freq.setdefault(saving, 0)
        savings_freq[saving] += 1

    print(savings_freq)
    return sum(savings_freq[k] for k in savings_freq.keys() if k >= 100)


def part2(input):
    answer = '...'
    return answer


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        # assert ans == None, "Got: {}".format(ans)
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
