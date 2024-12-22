import re
from itertools import permutations
from pathlib import Path

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


def complexity(door_code, button_presses):
    print(f"{door_code}: {''.join(button_presses)} ({len(button_presses)})")
    return int(re.findall(r'(\d+)', door_code)[0]) * len(button_presses)


def wont_panic(button, _movements):
    movements = list(_movements)
    if button == up and movements[:1] == [left]:
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


def arrow_pad_to_robot(buttons):
    def wont_panic(button, movements):
        if button == up and movements[:1] == [left]:
            return False
        if button == press and movements[:2] == [left, left]:
            return False
        if button == left and movements[:1] == [up]:
            return False
        return True

    current_button = press
    movements = []
    for button in buttons:
        dx, dy = sub(arrow_pad_button_to_xy[button], arrow_pad_button_to_xy[current_button])
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
            current_button = button
            continue
        # prefer <v over v<
        if horizontal_direction == left and vertical_direction == down and wont_panic(current_button,
                                                                                      horizontal_movements):
            movements += horizontal_movements
            movements += vertical_movements
            movements += [press]
            current_button = button
            continue

        # Order such that you don't hover over panic square
        if current_button == left:
            movements += horizontal_movements
            movements += vertical_movements
        else:
            movements += vertical_movements
            movements += horizontal_movements
        movements += [press]
        current_button = button
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


def get_shortest_path(code):
    shortest_path = None
    tree = code_pad_to_robot_options_tree(code, 'A', ['root', []])
    all_paths = get_all_paths(tree, [])
    for path in all_paths:
        flat_path = [v for p in path for v in p]
        print('Flat path:', flat_path)
        second_robot_movements = arrow_pad_to_robot(flat_path)
        third_robot_movements = arrow_pad_to_robot(second_robot_movements)

        final_path = ''.join(third_robot_movements)
        if not shortest_path or len(shortest_path) > len(final_path):
            shortest_path = final_path

    assert shortest_path, f'No shortest path found for: {code}'
    return shortest_path


def part1(input: str):
    codes = parse(input)
    # codes = ['179A']
    shortest_paths = []
    for code in codes:
        shortest_path = get_shortest_path(code)
        shortest_paths.append((code, shortest_path))
    return sum([complexity(code, path) for code, path in shortest_paths])

    # all_code_to_final_movements = []
    # for code in codes:
    #     first_robot_movements = code_pad_to_robot_options(code)
    #     print('First robot:', ''.join(first_robot_movements))
    #     second_robot_movements = arrow_pad_to_robot(first_robot_movements)
    #     print('Second robot:', ''.join(second_robot_movements))
    #     third_robot_movements = arrow_pad_to_robot(second_robot_movements)
    #     print('Third robot:', ''.join(third_robot_movements))
    #     You press the buttons for the third robot
    # all_code_to_final_movements.append((code, third_robot_movements))
    # return sum([complexity(code, button_presses) for code, button_presses in all_code_to_final_movements])


def part2(input):
    answer = '...'
    return answer


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
    # assert ans == None, "Got: {}".format(ans) # 265196 too high, 253968 too high
        print(f'Pt1::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    # with timer():
    #     ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2::ans: {ans}')
    #     ans = None


if __name__ == "__main__":
    run()
