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
    return []

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


def easy_swaps(input):
    wires, circuit = parse(input)
    swaps = []
    for gate in circuit:
        # any z value that isn't coming out of an XOR is wrong except z45
        if gate.out.startswith('z') and gate.op != 'XOR' and gate.out != 'z45':
            print('z-swap:', gate)
            swaps.append(gate)
        # any AND out that doesn't go into an OR is wrong
        if gate.op == 'AND':
            possible_or = maybe_get_gate(circuit, inputs={gate.out}, op='OR')
            if not possible_or:
                print('and-swap:', gate)
                swaps.append(gate)
        # any OR that doesn't go into an AND and an XOR is wrong
        if gate.op == 'OR':
            possible_and = maybe_get_gate(circuit, inputs={gate.out}, op='AND')
            possible_xor = maybe_get_gate(circuit, inputs={gate.out}, op='XOR')
            if not possible_and or not possible_xor:
                print('or-swap:', gate)
                swaps.append(gate)

        if gate.op == 'XOR':
            # any XOR that has a x/y input that goes into an OR is wrong
            if any(True for i in gate.inputs if i.startswith('x')):
                possible_and = maybe_get_gate(circuit, inputs={gate.out}, op='AND')
                possible_xor = maybe_get_gate(circuit, inputs={gate.out}, op='XOR')
                if not possible_and or not possible_xor and gate.out != 'z00':
                    swaps.append(gate)
                    print('xor-swap:', gate)

                # any XOR that has a x/y input that doesn't go into an XOR and an AND is wrong
                possible_or = maybe_get_gate(circuit, inputs={gate.out}, op='OR')
                if possible_or:
                    print('xor-swap-2:', gate)
                    swaps.append(gate)

    # any xor that doesn't take x/y input and doesn't output a z is wrong
    x_ors_that_do_not_take_xy = [g for g in circuit if (not list(g.inputs)[0].startswith('x') and not list(g.inputs)[0].startswith('y')) and g.op == 'XOR']
    print('hmm:', x_ors_that_do_not_take_xy)
    for xor in x_ors_that_do_not_take_xy:
        if not xor.out.startswith('z'):
            print('xor-swap-3:', xor)
            swaps.append(xor)

    seen = set()
    final_swaps = []
    for s in swaps:
        if s.out in seen:
            continue
        seen.add(s.out)
        if s.out in ['gct', 'z45', 'z00']:
            continue
        final_swaps.append(s)
    return final_swaps



def part2(input):
    swaps = easy_swaps(input)
    print(swaps)
    wire_value_list, circuit = parse(input)
    # Ans from part 1
    initial_ans = run_machine(wire_value_list, circuit)
    assert initial_ans == 52728619468518, f'Expected 52728619468518, got {initial_ans}'

    assert len(swaps) == 8

    x = get_number_from_input(wire_value_list, 'x')
    y = get_number_from_input(wire_value_list, 'y')
    correct_ans = x + y

    initial_circuit = circuit[:]
    all_pairings = get_all_pairings(swaps)[:]
    maybe_fixed_circuit = circuit[:]
    for pairings in all_pairings:
        maybe_fixed_circuit = initial_circuit
        for pair in pairings:
            a, b = pair
            maybe_fixed_circuit = swap_gates(maybe_fixed_circuit, a, b)
        ans = run_machine(wire_value_list, maybe_fixed_circuit)
        if ans == correct_ans:
            return ','.join(sorted([p.out for pair in pairings for p in pair]))
    # swapped_wires = fix_machine(wire_value_list, circuit)
    # return ','.join(sorted(swapped_wires))
    return '¯\\_(ツ)_/¯'


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
