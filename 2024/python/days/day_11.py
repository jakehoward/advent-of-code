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


def solve_for_stones(input, num_iters):
    return sum([solve_for_stone(int(stone), num_iters) for stone in input.split()])


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = solve_for_stones(example, 25)
        assert ans == 55312, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = solve_for_stones(input, 25)
        assert ans == 200446, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = solve_for_stones(input, 75)
        assert ans == 238317474993392, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
