from pathlib import Path

from utils.misc import timer
from utils.read import read_input
from functools import cache

example = """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
""".strip()


def parse(input):
    towels_block, combos_block = input.split('\n\n')
    towels = towels_block.split(', ')
    combos = combos_block.split('\n')
    return towels, combos


@cache
def is_possible(towels, combo):
    if combo == '':
        return True
    for towel in towels:
        if combo.startswith(towel):
            if is_possible(towels, combo[len(towel):]):
                return True
    return False


def count_possible(towels, combos):
    return sum([1 for combo in combos if is_possible(tuple(towels), combo)])


@cache
def num_arrangements(towels, combo):
    if combo == '':
        return 1
    num_ways = 0
    for towel in towels:
        if combo.startswith(towel):
            num_ways += num_arrangements(towels, combo[len(towel):])
    return num_ways


def count_number_ways(towels, combos):
    return sum([num_arrangements(tuple(towels), combo) for combo in combos])


def part1(input):
    towels, combos = parse(input)
    return count_possible(towels, combos)


def part2(input):
    towels, combos = parse(input)
    return count_number_ways(towels, combos)


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 6, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 322, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    with timer():
        ans = part2(example)
        assert ans == 16, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part2(input)
        assert ans == 715514563508258, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
