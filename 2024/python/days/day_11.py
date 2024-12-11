from pathlib import Path

from utils.misc import timer
from utils.read import read_input
import math

example = """125 17"""

def calc_next_stones(stone):
    if stone == 0:
        return [1]
    # stone_log_10 = int(math.log(stone, 10))
    # if stone_log_10 % 2 == 1:
    #     factor = int(math.pow(10, (stone_log_10 + 1) // 2))
    #     lhs = stone // factor
    #     rhs = stone - lhs * factor
    #     return [lhs, rhs]
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


def solve_for_stone(start_stone, n_times, stone_iters_to_ans):
    if (start_stone, n_times) in stone_iters_to_ans:
        return stone_iters_to_ans[(start_stone, n_times)]

    stones = [start_stone]
    deferred = []
    seen = set()
    for remaining in range(n_times, 0, -1):
        next_stones = []
        for stone in stones:
            if stone in seen:
                deferred.append((stone, remaining))
                continue
            next_stones += calc_next_stones(stone)
            seen.add(stone)
        stones = next_stones
    max_brute_depth = 25
    ans = len(stones)

    for stone, iters in deferred:
        if iters > max_brute_depth:
            ans += solve_for_stone(stone, iters, stone_iters_to_ans)
        else:
            bf_ans = brute_force(stone, iters, stone_iters_to_ans)
            stone_iters_to_ans[(stone, iters)] = bf_ans
            ans += bf_ans
    stone_iters_to_ans[(start_stone, n_times)] = ans
    return ans

def better_solve_for_stone(start_stone, n_times, stone_iters_to_ans):
    if known_ans := stone_iters_to_ans.get((start_stone, n_times)):
        return known_ans
    deferred = []
    seen = set()
    stones = [start_stone]
    rem_iters = n_times
    while rem_iters > 0 and stones:
        next_stones = []
        for stone in stones:
            if stone in seen:
                deferred.append((stone, rem_iters))
                continue
            seen.add(stone)
            next_stones += calc_next_stones(stone)
        stones = next_stones
        rem_iters -= 1

    answer = len(stones) + sum([better_solve_for_stone(stone, num_iters, stone_iters_to_ans) for stone, num_iters in deferred])
    stone_iters_to_ans[(start_stone, n_times)] = answer
    return answer

# 814 1183689 0 1 766231 4091 93836 46
def part2(input, n_times):
    stones = [int(stone) for stone in input.split()]
    stone_to_num_iters_to_ans = {}
    ans = 0
    for i, stone in enumerate(stones):
        print(f'stone {i+1} of {len(stones)}')
        num_stones = better_solve_for_stone(stone, n_times, stone_to_num_iters_to_ans)
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
        # check_ans = part2(input, 25)
        # assert check_ans == 200446, "Pt CHECK, expected 200446, Got: {}".format(check_ans)
        # print('Check 25 passed')

        # check_30_ans = part2('0 25', 30)
        # bf_30_ans = brute_force(0, 30) + brute_force(25, 30)
        # assert check_30_ans == bf_30_ans, f"Got {check_30_ans}, Expected: {bf_30_ans}"
        # print('Check 30 passed')

        ans = part2(input, 75)
        assert ans == 238317474993392, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')

if __name__ == "__main__":
    run()
