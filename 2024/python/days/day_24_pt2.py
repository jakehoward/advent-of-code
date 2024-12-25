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


def x_in(n):
    return f'x{str(n).rjust(2, '0')}'


def y_in(n):
    return f'y{str(n).rjust(2, '0')}'


def z_out(n):
    return f'z{str(n).rjust(2, '0')}'


def run_machine(input_wire_values: tuple[Wire, ...], circuit: Circuit, err=-1) -> int:
    wire_values = {name: value for name, value in input_wire_values}
    remaining_gates = list(circuit)
    max_iters = len(remaining_gates)
    iters = 0
    while remaining_gates:
        iters += 1
        if iters > max_iters:
            return err
        for _ in range(len(remaining_gates)):  # pop from front and push to back (changing the order)
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


def get_gate_or_vomit(circuit: Circuit, inputs: set[str] = None, op=None, out=None) -> Gate:
    gate = maybe_get_gate(circuit, inputs=inputs, op=op, out=out)
    if not gate:
        raise Exception('No matching gates - (╯°□°)╯︵ ┻━┻')
    return gate


def maybe_get_gate(circuit: Circuit, inputs=None, op=None, out=None) -> Gate:
    matching_gates = []
    for gate in circuit:
        inputs_match = True if not inputs else bool(gate.inputs.intersection(inputs))
        op_match = True if not op else gate.op == op
        out_match = True if not out else gate.out == out
        if inputs_match and op_match and out_match:
            matching_gates.append(gate)
    if not matching_gates:
        return None
    if len(matching_gates) > 1:
        raise Exception(f'{len(matching_gates)} matching gates, only one acceptable - (╯°□°)╯︵ ┻━┻')
    return matching_gates[0]


def t(x):
    return x is not None


def get_maybe_faulty_gates(circuit: Circuit, input_num=1, carry_gate: Gate = Gate({'x00', 'y00'}, 'AND', 'gct')):
    # https://en.wikipedia.org/wiki/Adder_(electronics)#/media/File:Fulladder.gif
    # Initial carry known from analysis that this is the first carry and the half adder before this is valid
    # last_carry = 'z45'
    if input_num == 45:
        # assert carry_gate.out == 'z45' # Probably can't rely on this as this last OR might end up swapped...
        return []

    maybe_faulty = set()
    inputs = {x_in(input_num), y_in(input_num)}
    # Inputs are never wrong, only outputs, so this will always find an XOR and an AND
    xor_1 = get_gate_or_vomit(circuit, inputs=inputs, op='XOR')
    and_2 = get_gate_or_vomit(circuit, inputs=inputs, op='AND')

    # Analyse xor2
    xor_2_by_carry = maybe_get_gate(circuit, inputs={carry_gate.out}, op='XOR')
    xor_2_by_xor_1 = maybe_get_gate(circuit, inputs={xor_1.out}, op='XOR')
    xor_2_both_truthy = t(xor_2_by_carry) and t(xor_2_by_xor_1)
    xor_2_equal = xor_2_by_carry == xor_2_by_xor_1
    xor_2_ok = False
    xor_2 = None
    if xor_2_both_truthy and xor_2_equal and xor_2_by_xor_1.out == z_out(input_num):
        xor_2_ok = True
        xor_2 = xor_2_by_xor_1
    else:
        maybe_faulty.add(carry_gate)
        maybe_faulty.add(xor_1)

    ## Analyse ANDs
    and_1_by_xor_1 = maybe_get_gate(circuit, inputs={xor_1.out}, op='AND')
    and_1_by_carry = maybe_get_gate(circuit, inputs={carry_gate.out}, op='AND')
    and_1_both_truthy = t(and_1_by_carry) and t(and_1_by_xor_1)
    and_1_equal = and_1_by_carry == and_1_by_xor_1
    and_1_ok = False
    and_1 = None
    if and_1_both_truthy and and_1_equal:
        and_1_ok = True
        and_1 = and_1_by_xor_1

    next_carry_by_and_1 = maybe_get_gate(circuit, inputs={and_1.out}, op='OR')
    next_carry_by_and_2 = maybe_get_gate(circuit, inputs={and_2.out}, op='OR')
    next_carry_both_truthy = t(next_carry_by_and_1) and t(next_carry_by_and_2)
    next_carry_equal = next_carry_by_and_1 == next_carry_by_and_2
    next_carry_ok = False
    next_carry = None
    if next_carry_both_truthy and next_carry_equal:
        next_carry_ok = True
        next_carry = next_carry_by_and_2
    else:
        and_1_ok = False
        maybe_faulty.add(carry_gate)
        maybe_faulty.add(xor_1)

    if t(next_carry):
        maybe_faulty |= get_maybe_faulty_gates(circuit, input_num + 1, next_carry)
    else:
        if not t(next_carry_by_and_1) and not t(next_carry_by_and_2):
            raise Exception('Cannot find a carry...')
        maybe_faulty |= get_maybe_faulty_gates(circuit, input_num + 1, next_carry_by_and_2)
        maybe_faulty |= get_maybe_faulty_gates(circuit, input_num + 1, next_carry_by_and_1)
    return maybe_faulty


def swap_gates(circuit: Circuit, a: Gate, b: Gate) -> Circuit:
    new_gates = []
    for gate in circuit:
        if gate == a or gate == b:
            continue
        new_gates.append(gate)
    new_gates.append(Gate(a.inputs, a.op, b.out))
    new_gates.append(Gate(b.inputs, b.op, a.out))
    return tuple(new_gates)


def get_all_pairings(items):
    if len(items) == 1 or len(items) == 0:
        return []
    first, *rest = items
    pairings = []
    for i, other in enumerate(rest):
        pair = (first, other)
        rec = rest[:]
        rec.pop(i)
        options = get_all_pairings(rec)
        for option in options:
            pairings.append([pair] + option)
        if not options:
            pairings.append([pair])
    return pairings


def get_all_four_pairings(items):
    if len(items) < 8:
        raise Exception('Not enough items')
    if len(items) == 8:
        return get_all_pairings(items)
    if len(set(items)) != len(items):
        raise Exception('Duplicate items')

    # get all ways to hold out extra items
    ways_to_hold_out_n = combinations(items, len(items) - 8)
    all_pairings = []
    for items_to_withhold in ways_to_hold_out_n:
        rem_items = [item for item in items if item not in items_to_withhold]
        all_pairings += get_all_pairings(rem_items)
    return all_pairings


def fix_machine(initial_wires: WireList, initial_circuit: Circuit):
    # returns which wires were swapped
    x = get_number_from_input(initial_wires, 'x')
    y = get_number_from_input(initial_wires, 'y')
    correct_ans = x + y

    faulty_gates = list(get_maybe_faulty_gates(initial_circuit))
    all_pairings = get_all_pairings(faulty_gates[:])
    for pairings in all_pairings:
        maybe_fixed_circuit = initial_circuit
        for pair in pairings:
            a, b = pair
            maybe_fixed_circuit = swap_gates(maybe_fixed_circuit, a, b)
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
