from pathlib import Path
from functools import cache

from utils.misc import timer
from utils.read import read_input

example = """125 17"""


def calc_next_stones(stone):
    if stone == 0:
        return [1]
    str_stone = str(stone)
    if len(str_stone) % 2 == 0:
        return [int(str_stone[0: len(str_stone) // 2]), int(str_stone[len(str_stone) // 2:])]
    return [stone * 2024]


@cache
def solve_for_stone(start_stone, iters):
    if iters == 0:
        return 1
    ans = sum([solve_for_stone(stone, iters - 1) for stone in calc_next_stones(start_stone)])
    return ans


def part1(input):
    return sum([solve_for_stone(int(stone), 25) for stone in input.split()])


def part2(input):
    return sum([solve_for_stone(int(stone), 75) for stone in input.split()])


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 55312, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 200446, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans == 238317474993392, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
