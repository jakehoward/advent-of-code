from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """3   4
4   3
2   5
1   3
3   9
3   3"""

def parse_input(input):
    list_a = []
    list_b = []
    for line in input.splitlines():
        [a, b] = line.split('   ')
        list_a.append(int(a))
        list_b.append(int(b))
    return list_a, list_b

def part1(input):
    list_a, list_b = parse_input(input)
    list_a.sort()
    list_b.sort()
    diffs = [abs(a - b) for [a, b] in zip(list_a, list_b)]
    answer = sum(diffs)
    print(f'Pt1::ans: {answer}')

def part2(input):
    list_a, list_b = parse_input(input)
    b_freqs = {}
    for num in list_b:
        b_freqs[num] = b_freqs.get(num, 0) + 1
    answer = 0;
    for id in list_a:
        answer += b_freqs.get(id, 0) * id
    print(f'Pt2::ans: {answer}')


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        parse_input(input)
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
