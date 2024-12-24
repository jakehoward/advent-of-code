from itertools import combinations
from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """
x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj""".strip()


def parse(input):
    initial_chunk, graph_chunk = input.split('\n\n')
    initial = [(name, bool(int(value))) for name, value in [line.split(': ') for line in initial_chunk.splitlines()]]
    graph = []
    for line in graph_chunk.splitlines():
        input, out = line.split(' -> ')
        a, op, b = input.split()
        graph.append(((a, op, b), out))
    return initial, graph


def part1(input):
    initial, graph = parse(input)
    wire_values = {name: value for name, value in initial}
    remaining = graph
    while remaining:
        for _ in range(len(remaining)):
            conn = remaining.pop(0)
            (a, op, b), out = conn
            if a in wire_values and b in wire_values:
                if op == 'AND':
                    wire_values[out] = wire_values[a] and wire_values[b]
                elif op == 'OR':
                    wire_values[out] = wire_values[a] or wire_values[b]
                elif op == 'XOR':
                    wire_values[out] = wire_values[a] ^ wire_values[b]
            else:
                remaining.append(conn)

    z_name_val_pairs = sorted([(name, int(value)) for name, value in wire_values.items() if name.startswith('z')],
                              key=lambda x: x[0], reverse=True)
    binary_string = ''.join([str(val) for name, val in z_name_val_pairs])
    # print(z_name_val_pairs, binary_string)
    answer = int(binary_string, 2)
    return answer



def part2(input):
    input, graph = parse(input)
    x_name_val_pairs = sorted([(name, int(value)) for name, value in input if name.startswith('x')],
                              key=lambda x: x[0], reverse=True)
    print(x_name_val_pairs)
    # wire_values = {name: value for name, value in input}
    # outputs = [out for _, out in graph]
    # pairs = list(combinations(outputs, 2))
    # print('pairs:', len(pairs), 'four pairs:', len(list(combinations(pairs, 4))))
    answer = '...'
    return answer


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 2024, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    with timer():
        ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2::ans: {ans}')
    #     ans = None


if __name__ == "__main__":
    run()
