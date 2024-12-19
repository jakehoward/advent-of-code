from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0""".strip()

example_2 = """
Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0
""".strip()

parsed_example_2 = (2024, 0, 0, '0,3,5,4,3,0')

parsed_example = (729, 0, 0, '0,1,5,4,3,0')

full_input = """
Register A: 47006051
Register B: 0
Register C: 0

Program: 2,4,1,3,7,5,1,5,0,3,4,3,5,5,3,0""".strip()

parsed_full_input = (47006051, 0, 0, '2,4,1,3,7,5,1,5,0,3,4,3,5,5,3,0')


def compute(a, b, c, symbols):
    A, B, C = a, b, c
    instr = 0
    out_buffer = []

    def combo(op):
        nonlocal A, B, C
        if op < 4:
            return op
        if op == 4:
            return A
        if op == 5:
            return B
        if op == 6:
            return C
        if op == 7:
            raise Exception('Not a valid combo op: 7')

    def adv(op):
        nonlocal A, instr
        A = A // (2 ** combo(op))
        instr += 2

    def bxl(op):
        nonlocal B, instr
        B = B ^ op
        instr += 2

    def bst(op):
        nonlocal B, instr
        B = combo(op) % 8
        instr += 2

    def jnz(op):
        nonlocal A, instr
        if A != 0:
            instr = op
        else:
            instr += 2

    def bxc(op):
        nonlocal B, C, instr
        B = B ^ C
        instr += 2

    def out(op):
        nonlocal out_buffer, instr, symbols
        out_buffer.append(combo(op) % 8)
        instr += 2

    def bdv(op):
        nonlocal A, B, instr
        B = A // (2 ** combo(op))
        instr += 2

    def cdv(op):
        nonlocal A, C, instr
        C = A // (2 ** combo(op))
        instr += 2

    instructions = {
        0: adv,
        1: bxl,
        2: bst,
        3: jnz,
        4: bxc,
        5: out,
        6: bdv,
        7: cdv,
    }

    while instr < len(symbols):
        instructions[symbols[instr]](symbols[instr + 1])

    return out_buffer


def part1(input):
    A, B, C, program = input
    symbols = [int(c) for c in program.split(',')]
    out_buffer = compute(A, B, C, symbols)
    return ','.join(map(str, out_buffer))


# Last value of B must be 0 or 8 (so that B % 8 prints 0)
# A must be zero so that it exits the loop. That means last value of A must be 0 - 7 (so A // (2**3) -> 0)
# It gets divided down by 2**3 on each loop (and you get a character printed per loop)
# => Last A is 6 for my program
def part2(input, example=False):
    a, B, C, program = input
    symbols = [int(c) for c in program.split(',')]
    print('Searching for:', program)

    options = [6]
    program_length = len(symbols)
    for p in range(1, program_length):
        new_options = []
        for option in options:
            next_base = option * 8
            for i in range(8):
                new_opt = next_base + i
                # print(new_opt, ":", 'Looking for:', symbols[-(p + 1):], 'Got: ', compute(new_opt, 0, 0, symbols))
                if symbols[-(p + 1):] == compute(new_opt, 0, 0, symbols):
                    new_options.append(new_opt)
        options = new_options
    return min(options)


def run():
    input = parsed_full_input
    with timer():
        ans = part1(parsed_example)
        assert ans == '4,6,3,5,6,3,5,2,1,0', "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == '6,2,7,2,3,1,6,0,5', "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    with timer():
        ans = part2(input)
        assert ans == 236548287712877, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
