from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """HERE"""

def part1(input):
    answer = '...'
    print(f'Pt1::ans: {answer}')

def part2(input):
    answer = '...'
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        part1(example)

    # with timer():
    #     part1(input)

    # with timer():
    #     part2(example)

    # with timer():
    #     part2(input)

if __name__ == "__main__":
    run()