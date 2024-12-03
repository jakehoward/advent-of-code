from pathlib import Path

from utils.misc import timer
from utils.read import read_input
import re

# example = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"""
# example_2 = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"""
# foo = re.findall(r"mul\(\d{0,3},\d{0,3}\)", example)
# next(re.finditer(r'mul\((\d{0,3}),(\d{0,3})\)', example_2)).start()
# example[len(example):]

def part1(input):
    pattern = re.compile(r'mul\((\d{0,3}),(\d{0,3})\)')
    answer = sum([int(a) * int(b) for a, b in re.findall(pattern, input)])
    print(f'Pt1::ans: {answer}')

def part2(input):
    pattern = re.compile(r'mul\((\d{0,3}),(\d{0,3})\)')
    i = 0
    muls = []
    on = True
    while (i < len(input)):
        if input[i:].startswith('do()'):
            on = True
        elif input[i:].startswith("don't()"):
            on = False
        else:
            if on:
                match = pattern.match(input[i:])
                if match:
                    muls.append(int(match.group(1)) * int(match.group(2)))
        i += 1
    answer = sum(muls)
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