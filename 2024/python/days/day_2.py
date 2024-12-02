from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9"""

def is_safe(a, b, descending):
    if a == b:
        return False
    if descending:
        if a < b or a - b > 3:
            return False
    else:
        if a > b or b - a > 3:
            return False
    return True

def one_missing_perms(xs):
    perms = []
    for idx in range(len(xs)):
        xs_copy = xs[:]
        del xs_copy[idx]
        perms.append(xs_copy)
    return perms

def part1(input):
    lines = input.splitlines()
    report = [list(map(int, line.split(' '))) for line in lines]
    num_safe = 0
    for levels in report:
        pairs = list(zip(levels[:-1], levels[1:]))
        safe = True
        descending = True if pairs[0][0] - pairs[0][1] > 0 else False
        for a, b in pairs:
            if not is_safe(a, b, descending):
                safe = False
                break
        if safe:
            num_safe += 1
    answer = num_safe
    print(f'Pt1::ans: {answer}')

def part2(input):
    lines = input.splitlines()
    report = [list(map(int, line.split(' '))) for line in lines]
    num_safe = 0
    for levels in report:
        for perms in one_missing_perms(levels):
            pairs = list(zip(perms[:-1], perms[1:]))
            safe = True
            descending = True if pairs[0][0] - pairs[0][1] > 0 else False
            for a, b in pairs:
                if not is_safe(a, b, descending):
                    safe = False
                    break
            if safe:
                num_safe += 1
                break
    answer = num_safe
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
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