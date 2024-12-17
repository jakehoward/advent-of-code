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
    while instr < len(symbols):
        if symbols[instr] == 0:
            op = symbols[instr + 1]
            A = A // (2 ** combo(op))
            instr += 2
            continue

        if symbols[instr] == 1:
            op = symbols[instr + 1]
            B = B ^ op
            instr += 2
            continue

        if symbols[instr] == 2:
            op = symbols[instr + 1]
            B = combo(op) % 8
            instr += 2
            continue

        if symbols[instr] == 3:
            op = symbols[instr + 1]
            if A != 0:
                instr = op
            else:
                instr += 2
            continue

        if symbols[instr] == 4:
            B = B ^ C
            instr += 2
            continue

        if symbols[instr] == 5:
            op = symbols[instr + 1]
            out_buffer.append(combo(op) % 8)
            if symbols[0:len(out_buffer)] != out_buffer:
                instr = len(symbols)  # halt condition
            instr += 2
            continue

        if symbols[instr] == 6:
            op = symbols[instr + 1]
            B = A // (2 ** combo(op))
            instr += 2
            continue

        if symbols[instr] == 8:
            op = symbols[instr + 1]
            C = A // (2 ** combo(op))
            instr += 2
            continue

    return ",".join([str(x) for x in out_buffer])


def part1(input):
    A, B, C, program = input
    return compute(A, B, C, program)



def part2(input):
    # for m in range(1, 18):
    #     print(m, [ x % m for x in [2707053, 2707135, 6901357, 6901439, 11095661, 11095743, 15289965, 15290047, 19484269, 19484269, 19484351, 19484351, 23678573, 23678655, 27872877, 27872959, 32067181, 32067263, 32132717, 32132799, 36261485, 36261567, 40455789, 40455871, 44650093, 44650175, 48844397, 48844479, 53038701, 53038783, 57233005, 57233087, 61427309, 61427391, 65621613, 65621695, 65687149, 65687231, 69815917, 69815999, 74010221, 74010303, 78204525, 78204607]])
    a, B, C, program = input
    symbols = [int(c) for c in program.split(',')]
    print('Searching for:', program)
    # for A in range(100_000_000):
    # for A in reversed([2707053, 2707135, 6901357, 6901439, 11095661, 11095743, 15289965, 15290047, 19484269, 19484269, 19484351, 19484351, 23678573, 23678655, 27872877, 27872959, 32067181, 32067263, 32132717, 32132799, 36261485, 36261567, 40455789, 40455871, 44650093, 44650175, 48844397, 48844479, 53038701, 53038783, 57233005, 57233087, 61427309, 61427391, 65621613, 65621695, 65687149, 65687231, 69815917, 69815999, 74010221, 74010303, 78204525, 78204607]):
    A = 8**16
    while True:
        # m = A % 8
        # if m != 5 and m != 7:
        #     continue
        # if (3 ^ (A // (2**6))) % 8 != 2 and (1 ^ (A // (2**4))) % 8 != 2:
        #     A += 1
        #     continue
        if A % 1_000_000 == 0:
            print('A:', A)
        out = fast_compute(A, B, C, symbols)
        # if A < 100:
        #     print('A:', A, 'out:', out)
        if out == program:
            print('!! Answer is:', A)
            return A
        A += 1
    print('A_replicator not found')
    return None

    # a, B, C, program = input
    # print('Analysis:', compute(117740, B, C, program, False))
    # print('Analysis:', compute(1, B, C, program, False))
    # print('Analysis:', compute(1, B, C, program, False))
    # print('Analysis:', compute(2, B, C, program, False))
    # print('Analysis:', compute(3, B, C, program, False))
    # print('Analysis:', compute(4, B, C, program, False))
    # print('Analysis:', compute(5, B, C, program, False))
    # print('Analysis:', compute(6, B, C, program, False))
    # print('Analysis:', compute(7, B, C, program, False))
    # return None


def run():
    # day = Path(__file__).name.split('.')[0].split('_')[-1]
    # input = read_input(day)
    input = parsed_full_input
    # with timer():
    #     ans = part1(parsed_example)
    #     assert ans == '4,6,3,5,6,3,5,2,1,0', "Got: {}".format(ans)
    #     print(f'Pt1(example)::ans: {ans}')
    #     ans = None

    # with timer():
    #     ans = part1(input)
    #     assert ans == '6,2,7,2,3,1,6,0,5', "Got: {}".format(ans)
    #     print(f'Pt1::ans: {ans}')
    #     ans = None

    # with timer():
    #     ans = part2(parsed_example_2)
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
