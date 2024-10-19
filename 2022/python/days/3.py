import itertools
from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw"""

def part1(input):
    lines = input.splitlines()
    total = 0
    for line in lines:
        l, r = [line[:len(line)//2], line[len(line)//2:]]
        s, = set(l) & set(r)
        if s.isupper():
            total += ord(s) - ord('A') + 27
        else:
            total += ord(s) - ord('a') + 1
        # total += ord(s) - (ord('A') - 26 if s.isupper() else ord('a')) + 1
    answer = total
    print(f'Pt1::ans: {answer}')

def part2(input):
    lines = input.splitlines()
    total = 0
    for a,b,c in itertools.batched(lines, 3):
        s, = set(a) & set(b) & set(c)
        if s.isupper():
            total += ord(s) - ord('A') + 27
        else:
            total += ord(s) - ord('a') + 1
    answer = total
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0]
    input = read_input(day)
    with timer():
        part1(example)

    with timer():
        part1(input)

    with timer():
        part2(example)

    with timer():
        part2(input)

if __name__ == "__main__":
    run()