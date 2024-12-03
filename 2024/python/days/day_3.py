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