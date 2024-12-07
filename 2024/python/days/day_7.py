from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20"""

def parse(input):
    inputs = []
    for line in input.splitlines():
        ans, snums = line.split(': ')
        nums = [int(n) for n in snums.split(' ')]
        inputs.append((int(ans), nums))
    return inputs

def part1(input):
    ans_nums_list = parse(input)
    def is_possible(ans, nums, total=0):
        if len(nums) == 0:
            return total == ans
        next_num = nums[0]
        mul_ans = is_possible(ans, nums[1:], total * next_num)
        add_ans = is_possible(ans, nums[1:], total + next_num)
        return mul_ans or add_ans

    possible_ans = []
    for ans, nums in ans_nums_list:
        if is_possible(ans, nums):
            possible_ans.append(ans)
    return sum(possible_ans)

def part2(input):
    ans_nums_list = parse(input)

    def is_possible(ans, nums, total=0):
        if len(nums) == 0:
            return total == ans
        next_num = nums[0]
        mul_ans = is_possible(ans, nums[1:], total * next_num)
        add_ans = is_possible(ans, nums[1:], total + next_num)
        con_ans = is_possible(ans, nums[1:], int(str(total) + str(next_num)))
        return mul_ans or add_ans or con_ans

    possible_ans = []
    for ans, nums in ans_nums_list:
        if is_possible(ans, nums):
            possible_ans.append(ans)
    return sum(possible_ans)

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 3749
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 2437272016585
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = part2(example)
        assert ans == 11387
        print(f'Pt2_v2(example)::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans == 162987117690649
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()