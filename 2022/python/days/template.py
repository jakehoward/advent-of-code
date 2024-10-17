from pathlib import Path
from utils.read import read_input

example = """HERE"""

def part1(input):
    answer = '...'
    print(f'Pt1::ans: {answer}')

def part2(input):
    answer = '...'
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0]
    input = read_input(day)
    part1(example)
    # part1(input)
    # part2(example)
    # part2(input)

if __name__ == "__main__":
    run()