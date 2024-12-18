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


def compute(a, b, c, program):
    A, B, C = a, b, c
    instr = 0
    symbols = [int(c) for c in program.split(',')]
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

    return ",".join([str(x) for x in out_buffer])


def fast_compute(a, b, c, symbols):
    A, B, C = a, b, c
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

    instr = 0
    abc = []
    while instr < len(symbols):
        op = symbols[instr + 1]
        opcode = symbols[instr]

        if opcode == 0:
            A = A // (2 ** combo(op))
            instr += 2
            continue

        if opcode == 1:
            B = B ^ op
            instr += 2
            continue

        if opcode == 2:
            B = combo(op) % 8
            instr += 2
            continue

        if opcode == 3:
            if A != 0:
                instr = op
            else:
                instr += 2
            continue

        if opcode == 4:
            B = B ^ C
            instr += 2
            continue

        if opcode == 5:
            out_buffer.append(combo(op) % 8)
            abc.append((A, B, C))
            if out_buffer != symbols[0:len(out_buffer)]:
                if len(out_buffer) > 7:
                    print(abc)
                    print('Failed::', 'A:', A, 'B:', B, 'C:', C, symbols, out_buffer)
                break
            instr += 2
            continue

        if opcode == 6:
            B = A // (2 ** combo(op))
            instr += 2
            continue

        if opcode == 7:
            C = A // (2 ** combo(op))
            instr += 2
            continue

    return ",".join([str(x) for x in out_buffer])


def part1(input):
    A, B, C, program = input
    symbols = [int(c) for c in program.split(',')]
    return compute(A, B, C, program)


def part2(input, example=False):
    a, B, C, program = input
    symbols = [int(c) for c in program.split(',')]
    print('Searching for:', program)
    # A = 8**15 if not example else 0
    # while A < (8**16) - 1:
    # Last value of A must be 0 or 8 (so that B % 8 prints 0)
    # - which must be zero so it exits the loop
    # it gets divided down by 2**3 on each printing of a character
    # For the last value to be zero, it must be 1-7 in order to be divided by 8
    # and become 0, so should be in the range
    A = 1 * (8*14)
    while A < (7 * (8**14)):
        if A % 1_000_000 == 0: # check == (7*(8**14))
            print('A:', A)
        out = fast_compute(A, B, C, symbols)
        if out == program:
            print('!! Answer is:', A)
            return A
        A += 1
    print('A_replicator not found')
    return None


def run():
    # day = Path(__file__).name.split('.')[0].split('_')[-1]
    # input = read_input(day)
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

    # with timer():
    #     ans = part2(parsed_example_2, True)
    #     assert ans == 117440, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    with timer():
        ans = part2(input)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
