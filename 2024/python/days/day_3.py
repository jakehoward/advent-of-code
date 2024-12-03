from pathlib import Path

from utils.misc import timer
from utils.read import read_input
import re

example = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"""
example_2 = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"""

pattern = re.compile(r'mul\((\d{0,3}),(\d{0,3})\)')

def part1(input):
    answer = sum([int(a) * int(b) for a, b in re.findall(pattern, input)])
    print(f'Pt1::ans: {answer}')

def part2(input):
    i = 0
    answer = 0
    on = True
    while i < len(input):
        if input[i:].startswith('do()'):
            on = True
        elif input[i:].startswith("don't()"):
            on = False
        elif on:
            match = pattern.match(input[i:])
            if match:
                answer += int(match.group(1)) * int(match.group(2))
        i += 1
    print(f'Pt2::ans: {answer}')

def part2_fast(input):
    pos = 0
    on = True
    input_len = len(input)
    answer = 0
    while pos < input_len:
        if input[pos:pos +4] == "do()":
            on = True
            pos += 4
        elif input[pos:pos + 7] == "don't()":
            on = False
            pos += 7
        elif on:
            if input[pos:].startswith("mul("):
                pos += 4
                next_comma_idx = input[pos:].find(',')
                next_paren_idx = input[pos:].find(')')
                mul_len = next_paren_idx
                if next_comma_idx != -1 and next_paren_idx != -1 and next_comma_idx >= 1 and next_paren_idx > next_comma_idx and 3 <= mul_len and mul_len <= 7:
                    first = input[pos:pos + next_comma_idx]
                    second = input[pos + next_comma_idx + 1: pos + next_paren_idx]
                    if first.isdigit() and second.isdigit():
                        answer += int(first) * int(second)
                        pos += mul_len
            else:
                pos += 1
        else:
            pos += 1
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        part1(example)

    with timer():
        part1(input)

    with timer():
        part2(example_2)

    with timer():
        part2(input)

    with timer():
        part2_fast(input)

if __name__ == "__main__":
    run()

# Pt1::ans: 161
# Elapsed time: 17 microseconds
# Pt1::ans: 170068701
# Elapsed time: 231 microseconds
# Pt2::ans: 48
# Elapsed time: 24 microseconds
# Pt2::ans: 78683433
# Elapsed time: 12 ms