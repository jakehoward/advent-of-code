from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """
1
10
100
2024""".strip()

example_2 = """
1
2
3
2024""".strip()


def next_secret_number(n):
    r1 = ((n * 64) ^ n) % 16777216
    r2 = ((r1 // 32) ^ r1) % 16777216
    r3 = ((r2 * 2048) ^ r2) % 16777216
    return r3


def part1(input):
    numbers = [int(n) for n in input.splitlines()]
    for _ in range(2000):
        for i, n in enumerate(numbers):
            numbers[i] = next_secret_number(n)
    answer = sum(numbers)
    return answer


def get_last_digit(n):
    return n - ((n // 10) * 10)


def get_all_secret_numbers(n, num_to_generate=2000):
    numbers = [n]
    for i in range(num_to_generate):
        numbers.append(next_secret_number(numbers[i]))
    return numbers


def get_sequence_to_num_bananas(all_sn):
    last_digits = [get_last_digit(n) for n in all_sn]
    diffs = [b - a for a, b in zip(last_digits, last_digits[1:])]

    seq_to_max_bananas = {}
    for i, n in enumerate(last_digits):
        if i < 4:
            continue
        sequence = tuple(diffs[i - 4:i])
        if sequence in seq_to_max_bananas:
            continue
        seq_to_max_bananas[sequence] = n
    return seq_to_max_bananas


def part2(input):
    seed_numbers = [int(n) for n in input.splitlines()]
    all_sequence_to_max_bananas = [get_sequence_to_num_bananas(get_all_secret_numbers(seed)) for seed in seed_numbers]
    all_sequences = set([k for m in all_sequence_to_max_bananas for k in m.keys()])

    max_banana_count = -1
    for sequence in all_sequences:
        banana_count = sum([s_to_max.get(sequence, 0) for s_to_max in all_sequence_to_max_bananas])
        if banana_count > max_banana_count:
            max_banana_count = banana_count

    return max_banana_count


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 37327623, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 19847565303, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    with timer():
        ans = part2(example_2)
        assert ans == 23, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part2(input)
        assert ans == 2250, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
