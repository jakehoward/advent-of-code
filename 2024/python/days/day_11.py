from pathlib import Path

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


def brute_force(start_stone, n_times, stone_iters_to_ans=None):
    if stone_iters_to_ans and stone_iters_to_ans.get((start_stone, n_times)):
        return stone_iters_to_ans[(start_stone, n_times)]

    stones = [start_stone]
    for i in range(n_times):
        next_stones = []
        for stone in stones:
            next_stones += calc_next_stones(stone)
        if stone_iters_to_ans: stone_iters_to_ans[(start_stone, i + 1)] = len(next_stones)
        stones = next_stones
    answer = len(stones)
    return answer


def part1(input):
    stones = [int(stone) for stone in input.split()]
    ans = 0
    for stone in stones:
        ans += brute_force(stone, 25)
    return ans


def solve_for_stone(start_stone, iters, stone_iters_to_ans):
    if known_ans := stone_iters_to_ans.get((start_stone, iters)):
        return known_ans
    if iters == 0:
        return 1
    ans = sum([solve_for_stone(stone, iters - 1, stone_iters_to_ans) for stone in calc_next_stones(start_stone)])
    stone_iters_to_ans[(start_stone, iters)] = ans
    return ans


def part2(input, n_times):
    stones = [int(stone) for stone in input.split()]
    stone_to_num_iters_to_ans = {}
    ans = 0
    for i, stone in enumerate(stones):
        print(f'stone {i + 1} of {len(stones)}')
        num_stones = solve_for_stone(stone, n_times, stone_to_num_iters_to_ans)
        ans += num_stones
    return ans


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
        ans = part2(input, 75)
        assert ans == 238317474993392, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
