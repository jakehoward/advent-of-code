from collections import namedtuple
from itertools import combinations

from utils.misc import timer
from utils.read import read_input

Gate = namedtuple('Gate', ['inputs', 'op', 'out'])
Wire = namedtuple('WireValue', ['name', 'state'])
type Circuit = tuple[Gate, ...]
type WireList = tuple[Wire, ...]


def parse(input):
    initial_chunk, graph_chunk = input.split('\n\n')
    wires = [Wire(name, bool(int(value))) for name, value in [line.split(': ') for line in initial_chunk.splitlines()]]
    circuit = []
    for line in graph_chunk.splitlines():
        input, out = line.split(' -> ')
        a, op, b = input.split()
        circuit.append(Gate({a, b}, op, out))
    return tuple(wires), tuple(circuit)


def run_machine(input_wire_values: tuple[Wire, ...], circuit: Circuit) -> int:
    wire_values = {name: value for name, value in input_wire_values}
    remaining_gates = list(circuit)
    while remaining_gates:
        for _ in range(len(remaining_gates)):
            gate = remaining_gates.pop(0)
            a, b = list(gate.inputs)
            if a in wire_values and b in wire_values:
                if gate.op == 'AND':
                    wire_values[gate.out] = wire_values[a] and wire_values[b]
                elif gate.op == 'OR':
                    wire_values[gate.out] = wire_values[a] or wire_values[b]
                elif gate.op == 'XOR':
                    wire_values[gate.out] = wire_values[a] ^ wire_values[b]
            else:
                remaining_gates.append(gate)

    z_name_val_pairs = sorted([(name, int(value)) for name, value in wire_values.items() if name.startswith('z')],
                              key=lambda x: x[0], reverse=True)
    binary_string = ''.join([str(val) for name, val in z_name_val_pairs])
    return int(binary_string, 2)


def get_name_val_pairs(wires, n):
    nv = [(name, int(value)) for name, value in wires if name.startswith(n)]
    return sorted(nv, key=lambda x: x[0], reverse=True)


def get_number_from_input(wires: WireList, wire_group: str) -> int:
    assert wire_group == 'x' or wire_group == 'y' or wire_group == 'z'
    name_val_pairs = get_name_val_pairs(wires, wire_group)
    binary_string = ''.join([str(val) for name, val in name_val_pairs])
    return int(binary_string, 2)


# Find gates that might be faulty
def get_maybe_faulty_gates(circuit: Circuit):
    pass


def swap_gates(circuit: Circuit, a: Gate, b: Gate) -> Circuit:
    pass



def get_all_pairings(items):
    pass


def fix_machine(initial_wires: WireList, initial_circuit: Circuit):
    # returns which wires were swapped
    x = get_number_from_input(initial_wires, 'x')
    y = get_number_from_input(initial_wires, 'y')
    correct_ans = x + y

    faulty_gates = get_maybe_faulty_gates(initial_circuit)
    all_pairings = get_all_pairings(faulty_gates)
    for pairings in all_pairings:
        maybe_fixed_circuit = None
        for pair in pairings:
            a, b = pair
            maybe_fixed_circuit = swap_gates(initial_circuit, a, b)
        ans = run_machine(initial_wires, maybe_fixed_circuit)
        if ans == correct_ans:
            return [p for pair in pairings for p in pair]

    assert False, "Didn't find nuffin, ¯\\_(ツ)_/¯"

def part2(input):
    wire_value_list, circuit = parse(input)
    # Ans from part 1
    initial_ans = run_machine(wire_value_list, circuit)
    assert initial_ans == 52728619468518, f'Expected 52728619468518, got {initial_ans}'

    swapped_wires = fix_machine(wire_value_list, circuit)
    return ','.join(sorted(swapped_wires))


def run():
    day = '24'
    input = read_input(day)

    with timer():
        ans = part2(input)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
