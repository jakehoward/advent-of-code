from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """HERE"""

def part1(input):
    answer = '...'
    return answer

def part2(input):
    answer = '...'
    return answer

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        print(f'Pt1(example)::ans: {part1(example)}')

    # with timer():
    #     print(f'Pt1::ans: {part1(input)}')

    # with timer():
    #     print(f'Pt2(example)::ans: {part2(example)}')

    # with timer():
    #     print(f'Pt2::ans: {part2(input)}')


if __name__ == "__main__":
    run()