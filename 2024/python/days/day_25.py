from pathlib import Path

from utils.grid import make_grid
from utils.misc import timer
from utils.read import read_input

example = """
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####""".strip()


def parse(input):
    chunks = input.split('\n\n')
    keys = []
    locks = []
    neither = []
    for chunk in chunks:
        if chunk.startswith('#####'):
            locks.append(make_grid(chunk))
        elif chunk.endswith('#####'):
            keys.append(make_grid(chunk))
        else:
            neither.append(chunk)
    assert len(neither) == 0
    return keys, locks


def get_height(_col):
    col = _col[:]
    og = len(col)
    while col[0] == '#': col.pop(0)
    return og - len(col) - 1


def part1(input):
    key_grids, lock_grids = parse(input)
    key_heights = []
    lock_heights = []
    for key in key_grids:
        cols = key.get_cols()[:]
        for col in cols: col.reverse()
        heights = [get_height(c) for c in cols]
        key_heights.append(heights)
    for lock in lock_grids:
        cols = lock.get_cols()[:]
        heights = [get_height(c) for c in cols]
        lock_heights.append(heights)
    potential_fits = 0
    for lock in lock_heights:
        for key in key_heights:
            if all([k + l < 6 for k, l in zip(key, lock)]):
                potential_fits += 1
    answer = potential_fits
    return answer


def part2(input):
    answer = '...'
    return answer


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 3, "Got: {}".format(ans)
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
