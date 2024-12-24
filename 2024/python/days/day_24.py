from collections import namedtuple
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
    circuit = []
    for line in graph_chunk.splitlines():
        input, out = line.split(' -> ')
        a, op, b = input.split()
        circuit.append(((a, op, b), out))
    return initial, tuple(circuit)


def run_machine(wire_value_list, _circuit):
    circuit = list(_circuit)
    wire_values = {name: value for name, value in wire_value_list}
    remaining_gates = circuit
    while remaining_gates:
        for _ in range(len(remaining_gates)):
            gate = remaining_gates.pop(0)
            (a, op, b), out = gate
            if a in wire_values and b in wire_values:
                if op == 'AND':
                    wire_values[out] = wire_values[a] and wire_values[b]
                elif op == 'OR':
                    wire_values[out] = wire_values[a] or wire_values[b]
                elif op == 'XOR':
                    wire_values[out] = wire_values[a] ^ wire_values[b]
            else:
                remaining_gates.append(gate)

    z_name_val_pairs = sorted([(name, int(value)) for name, value in wire_values.items() if name.startswith('z')],
                              key=lambda x: x[0], reverse=True)
    binary_string = ''.join([str(val) for name, val in z_name_val_pairs])
    return int(binary_string, 2)


def part1(input):
    wire_value_list, circuit = parse(input)
    return run_machine(wire_value_list, circuit)


def get_name_val_pairs(wire_value_list, n):
    nv = [(name, int(value)) for name, value in wire_value_list if name.startswith(n)]
    return sorted(nv, key=lambda x: x[0], reverse=True)


def get_number_from_input(wire_value_list, n):
    assert n == 'x' or n == 'y' or n == 'z'
    name_val_pairs = get_name_val_pairs(wire_value_list, n)
    binary_string = ''.join([str(val) for name, val in name_val_pairs])
    return int(binary_string, 2)

                          # in op in`  -> out
type RawGate = tuple[tuple[str, str, str], str]
type RawCircuit = tuple[RawGate]

Z_Output = namedtuple('Z_Output', ['name', 'number'])
Input = namedtuple('Input', ['name'])
Output = namedtuple('Output', ['name'])
Carry = namedtuple('Carry', ['name', 'to'])
Gate = namedtuple('Gate', ['inputs', 'op', 'out'])
Result = namedtuple('Result', ['swapped_gates', 'carry', 'fixed_circuit'])

def get_only_gate(circuit: RawCircuit, inputs: set[Input], op: str) -> Gate:
    # Get matching gate. Raise if no gate or multiple gates
    pass


def maybe_get_only_gate(circuit: RawCircuit, inputs: set[Input], op: str) -> Gate:
    # Get single matching gate or None, Raise if multiple gates
    pass


def trace_full_adder(initial_circuit: RawCircuit, x_in: Input, y_in: Input, carry: Carry) -> Result:
    circuit = initial_circuit
    swapped_gates = []


    ## First and second XOR
    xor_1 = get_only_gate(circuit, inputs={x_in, y_in}, op='XOR')
    xor_2 = maybe_get_only_gate(circuit, inputs={xor_1.out, carry.name}, op='XOR')
    # if xor_2 is None -> xor_1 has the wrong output. Find output that goes into xor with carry and swap
    # if xor_2.out is not Z[input_n], xor_2 has wrong output, find gate that outputs to z[input_n] and swap

    # Update fixed values
    xor_1 = get_only_gate(circuit, inputs={x_in, y_in}, op='XOR')
    xor_2 = get_only_gate(circuit, inputs={xor_1.out, carry.name}, op='XOR')

    ## First and second AND
    and_1 = get_only_gate(circuit, inputs={xor_1.out, carry.name}, op='AND')
    and_2 = get_only_gate(circuit, inputs={x_in, y_in}, op='AND')
    final_or = maybe_get_only_gate(circuit, inputs={and_1.out, and_2.out}, op='OR')
    # if not final_or and_1 or and_2, or both, have the wrong output, they should input to an OR (tricky?)
    # if final_or.out not z45 OR the input to an XOR and an AND, wrong output, needs to be swapped

    ## todo: (Final OR).out
    out_carry = final_or.out

    return Result(swapped_gates, out_carry, circuit)



def find_swapped_gates(wire_value_list, initial_circuit):
    x_wires = [name for name, _ in get_name_val_pairs(wire_value_list, 'x')]
    y_wires = [name for name, _ in get_name_val_pairs(wire_value_list, 'y')]
    x_wires.reverse()
    y_wires.reverse()
    xy_pairs = list(zip(x_wires, y_wires))

    all_swapped_gates = []
    latest_circuit = initial_circuit
    latest_carry = Carry('gct', 1) # Manual analysis
    for x_in, y_in in xy_pairs[1:]:
        result = trace_full_adder(latest_circuit, Input(x_in), Input(y_in), latest_carry)
        for a,b in result.swapped_gates:
            all_swapped_gates += [a, b]
        latest_carry = result.carry
        latest_circuit = result.fixed_circuit

    return ','.join(sorted(all_swapped_gates))


def analysis(wire_value_list, circuit):
    x = get_number_from_input(wire_value_list, n='x')
    y = get_number_from_input(wire_value_list, n='y')
    machine_before = run_machine(wire_value_list, circuit)
    # print('x:', x, 'y:', y, 'x+y:', x + y, 'machine:', machine_before, 'delta:', x + y - machine_before)

    print('Circuit:')
    g = []
    for gate in sorted(circuit):
        (a, op, b), out = gate
        if op == 'XOR' and (a.startswith('x') or a.startswith('y')):
            g.append(gate)
    print(len(g))


def part2(input):
    wire_value_list, circuit = parse(input)

    # analysis(wire_value_list, circuit)
    return find_swapped_gates(wire_value_list, circuit)


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
        assert ans == 52728619468518, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    with timer():
        ans = part2(input)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
