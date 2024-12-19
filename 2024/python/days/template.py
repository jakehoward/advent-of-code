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
        ans = part1(example)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part1(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt1::ans: {ans}')
    #     ans = None

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    # with timer():
    #     ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2::ans: {ans}')
    #     ans = None


if __name__ == "__main__":
    run()
