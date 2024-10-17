from pathlib import Path
from utils.read import read_input

example = """1000
2000
3000

4000

5000
6000

7000
8000
9000

10000"""

def part1(input):
    elves = input.split('\n\n')
    cals = map(lambda elf: sum([int(c) for c in elf.split('\n')]), elves)
    answer = max(cals)
    print(f'Pt1::ans: {answer}')

def part2(input):
    elves = input.split('\n\n')
    cals = map(lambda elf: sum([int(c) for c in elf.split('\n')]), elves)
    answer = sum(sorted(cals, reverse=True)[:3])
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0]
    input = read_input(day)

    part1(example)
    part1(input)
    part2(example)
    part2(input)

if __name__ == "__main__":
    run()