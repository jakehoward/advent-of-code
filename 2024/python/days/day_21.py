import re
import math
from itertools import permutations
from pathlib import Path
from functools import cache

from utils.misc import timer
from utils.read import read_input

example = """
029A
980A
179A
456A
379A""".strip()

example_expected = """
029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A""".strip()


def parse(input):
    return input.strip().splitlines()


up = "^"
down = "v"
left = "<"
right = ">"
press = "P"  # A in the problem explanation, but that's overloaded and confusing
panic = 'X'


def reverse_lookup(m):
    return {v: k for k, v in m.items()}


move_to_delta = {left: (-1, 0), down: (0, 1), right: (1, 0), up: (0, -1)}
delta_to_move = reverse_lookup(move_to_delta)

code_pad_button_to_xy = {'A': (2, 3), '0': (1, 3), panic: (0, 3),
                         '1': (0, 2), '2': (1, 2), '3': (2, 2),
                         '4': (0, 1), '5': (1, 1), '6': (2, 1),
                         '7': (0, 0), '8': (1, 0), '9': (2, 0)}
code_pad_xy_to_button = reverse_lookup(code_pad_button_to_xy)

arrow_pad_button_to_xy = {up: (1, 0), press: (2, 0), left: (0, 1), down: (1, 1), right: (2, 1), panic: (0, 0)}
arrow_pad_xy_to_button = reverse_lookup(arrow_pad_button_to_xy)


def sub(p1, p2):
    return p1[0] - p2[0], p1[1] - p2[1]


def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]


def complexity(door_code, button_presses, debug=False):
    if debug: print(f"{door_code}: {''.join(button_presses)} ({len(button_presses)})")
    return int(re.findall(r'(\d+)', door_code)[0]) * len(button_presses)


def complexity_v2(door_code, len_button_presses, debug=False):
    if debug: print(f"{door_code}: ({len_button_presses})")
    return int(re.findall(r'(\d+)', door_code)[0]) * len_button_presses


def wont_panic(button, _movements):
    movements = list(_movements)
    if button == up and movements[:1] == [left]:
        return False
    if button == press and movements[:2] == [left, left]:
        return False
    if button == left and movements[:1] == [up]:
        return False
    if button == 'A' and movements[:2] == [left, left]:
        return False
    if button == '0' and movements[:1] == [left]:
        return False
    if button == '1' and movements[:1] == [down]:
        return False
    return True


def get_movements_hv(from_button, to_button, pad_button_to_xy):
    dx, dy = sub(pad_button_to_xy[to_button], pad_button_to_xy[from_button])
    horizontal_direction = left if dx < 0 else right
    vertical_direction = up if dy < 0 else down

    horizontal_movements = [horizontal_direction] * abs(dx)
    vertical_movements = [vertical_direction] * abs(dy)
    return horizontal_movements, vertical_movements


def arrow_pad_to_robot(buttons, do_optimisation=True):
    current_button = press
    movements = []
    for next_button in buttons:
        dx, dy = sub(arrow_pad_button_to_xy[next_button], arrow_pad_button_to_xy[current_button])
        horizontal_direction = left if dx < 0 else right
        vertical_direction = up if dy < 0 else down

        horizontal_movements = [horizontal_direction] * abs(dx)
        vertical_movements = [vertical_direction] * abs(dy)

        # prefer v> over >v
        if horizontal_direction == right and vertical_direction == down and wont_panic(current_button,
                                                                                       vertical_movements):
            movements += vertical_movements
            movements += horizontal_movements
            movements += [press]
            current_button = next_button
            continue
        if do_optimisation:
            # prefer <v over v<
            if horizontal_direction == left and vertical_direction == down and wont_panic(current_button,
                                                                                          horizontal_movements):
                movements += horizontal_movements
                movements += vertical_movements
                movements += [press]
                current_button = next_button
                continue
            # prefer <^ over ^<
            if vertical_direction == up and horizontal_direction == left and wont_panic(current_button, horizontal_movements):
                movements += horizontal_movements
                movements += vertical_movements
                movements += [press]
                current_button = next_button
                continue

        # Order such that you don't hover over panic square
        if current_button == left:
            movements += horizontal_movements
            movements += vertical_movements
        else:
            movements += vertical_movements
            movements += horizontal_movements
        movements += [press]
        current_button = next_button
    return movements


def make_node(value, children):
    assert isinstance(value, tuple), isinstance(children, list)
    return [value, children]


def add_child(node, child):
    node[1].append(child)
    return node


def code_pad_to_robot_options_tree(remaining_code, current_button, tree):
    if not remaining_code:
        return tree

    next_button = remaining_code[0]
    horizontal_movements, vertical_movements = get_movements_hv(current_button, next_button, code_pad_button_to_xy)

    all_movements = horizontal_movements + vertical_movements
    all_permutations = [p + (press,) for p in list(set(permutations(all_movements))) if wont_panic(current_button, p)]

    for permutation in all_permutations:
        node = make_node(permutation, [])
        add_child(tree, node)
        code_pad_to_robot_options_tree(remaining_code[1:], next_button, node)
    return tree


def get_all_paths(tree, path_so_far):
    value, children = tree
    if not children:
        return [path_so_far + [value]]

    paths = []
    for child in children:
        paths += get_all_paths(child, path_so_far[:] + [value] if value != 'root' else [])
    return paths


def slow_get_shortest_path(code, num_robots, debug=False):
    if debug: print('Searching for shortest path for:', code)
    shortest_path = None
    tree = code_pad_to_robot_options_tree(code, 'A', ['root', []])
    all_paths = get_all_paths(tree, [])
    for path in all_paths:
        flat_path = [v for p in path for v in p]
        movements = arrow_pad_to_robot(flat_path)
        for i in range(num_robots -1):
            movements = arrow_pad_to_robot(movements, do_optimisation=False)

        final_path = ''.join(movements)
        if not shortest_path or len(shortest_path) > len(final_path):
            shortest_path = final_path

    assert shortest_path, f'No shortest path found for: {code}'
    return shortest_path


def partition_by_press(movements):
    partitions = []
    sub_partition = []
    for move in movements:
        if move == press:
            partitions.append(sub_partition + [press])
            sub_partition = []
        else:
            sub_partition.append(move)
    assert len(sub_partition) == 0
    return partitions


@cache
def get_num_moves(start_moves, num_robots):
    if num_robots == 0:
        return len(start_moves)
    partitions = partition_by_press(start_moves)
    path_length = sum([get_num_moves(tuple(arrow_pad_to_robot(p)), num_robots - 1) for p in partitions])
    return path_length


def get_shortest_path(code, num_robots, debug=False):
    if debug: print('Searching for shortest path for:', code)
    shortest_path_length = math.inf
    tree = code_pad_to_robot_options_tree(code, 'A', ['root', []])
    all_paths = get_all_paths(tree, [])
    for path in all_paths:
        flat_path = [v for p in path for v in p]
        path_length = get_num_moves(tuple(flat_path), num_robots)

        if shortest_path_length > path_length:
            shortest_path_length = path_length

    assert shortest_path_length != math.inf, f'No shortest path found for: {code}'
    return shortest_path_length


def get_ans_slow(input):
    codes = parse(input)
    shortest_paths = []
    for code in codes:
        shortest_path = slow_get_shortest_path(code, 2)
        shortest_paths.append((code, shortest_path))
    return sum([complexity(code, path) for code, path in shortest_paths])


def get_ans(input, num_robots):
    codes = parse(input)
    shortest_paths = []
    for code in codes:
        shortest_path_length = get_shortest_path(code, num_robots)
        shortest_paths.append((code, shortest_path_length))
    return sum([complexity_v2(code, path_len) for code, path_len in shortest_paths])


def part1(input: str):
    # return get_ans_slow(input)
    return get_ans_slow(input)


def part2(input):
    # Compare old and new
    # codes = parse(input)
    # for code in codes:
    #     print('code:', code)
    #     for i in range(1, 11):
    #         print('i:', i, 'slow:', len(slow_get_shortest_path(code, i)), 'fast:', get_shortest_path(code, i))

    return get_ans(input, 25)


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 126384, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input)
        assert ans == 248108, "Got: {}".format(ans)  # 265196 too high, 253968 too high
        print(f'Pt1::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    with timer():
        ans = part2(input)
        assert 228543993714790 < ans < 355902020229854, "Got: {}".format(ans)  # 563238807716320 too high
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()
