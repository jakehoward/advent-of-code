from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """19, 13, 30 @ -2,  1, -2
18, 19, 22 @ -1, -1, -2
20, 25, 34 @ -2, -2, -4
12, 31, 28 @ -1, -2, -1
20, 19, 15 @  1, -5, -3"""


def part1(input):
    answer = '...'
    print(f'Pt1::ans: {answer}')

def mul(v):
    return [c * -1 for c in v]

def are_parallel(v1, v2):
    assert len(v1) == len(v2), f"Parallel vectors have the same dimensional space, v1: {len(v1)} v2: {len(v2)}"
    # Vectors v1 and v2 are parallel if, v1 = C * v2
    return len(set(map(lambda p: p[0] / p[1] if p[1] != 0 else 0 if p[0] == 0 else False, zip(v1, v2)))) == 1

def part2(input):
    v0_v_pairs = [(tuple(map(int, start.split(', '))), tuple(map(int, v.split(', ')))) for start, v in
                    [line.split(' @ ') for line in input.splitlines()]]
    vecs = [v for v0, v in v0_v_pairs]
    positive_x = [v for v in vecs if v[0] > 1]
    negative_x = [v for v in vecs if v[0] < 1]
    vx_00 = positive_x[0]
    vx_01 = negative_x[0]


    for idx, v in enumerate(vecs):
        for idx2, v2 in enumerate(vecs):
            if idx != idx2 and are_parallel(v, v2):
                print(v, v2)

    # parallel = [k for k, v in freqs(vecs).items() if v >= 2]
    # print(parallel)
    answer = '...'
    print(f'Pt2::ans: {answer}')


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    # with timer():
    #     part1(example)

    # with timer():
    #     part1(input)

    # with timer():
    #     part2(example)

    # with timer():
    #     part2(input)


if __name__ == "__main__":
    run()
