from pathlib import Path
from functools import cache

from days.template import part1
from utils.misc import timer
from utils.read import read_input

example = """Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279"""


def parse_input(input):
    raw_games = input.split('\n\n')
    games = []
    for game in raw_games:
        A, B, prize = game.splitlines()
        a_xy = tuple([int(n.split('+')[1]) for n in A.split(': ')[1].split(', ')])
        b_xy = tuple([int(n.split('+')[1]) for n in B.split(': ')[1].split(', ')])
        prize_xy = tuple([int(n.split('=')[1]) for n in prize.split(': ')[1].split(', ')])
        games.append((a_xy, b_xy, prize_xy))
    return games


def solve(input):
    games = parse_input(input)
    max_presses_per_button = 100
    a_cost = 3
    b_cost = 1
    tokens_spent = 0
    for game in games:
        (ax, ay), (bx, by), (px, py) = game
        for num_a in range(max_presses_per_button + 1):
            x = px - num_a * ax
            y = py - num_a * ay
            num_x_presses = x // bx
            num_y_presses = y // by
            if x % bx == 0 and y % by == 0 and num_x_presses == num_y_presses and num_x_presses <= 100:
                tokens_spent += (num_a * a_cost) + ((x // bx) * b_cost)
                break
    answer = tokens_spent
    return answer


@cache
def solve_game(a_xy, b_xy, p_xy):
    a_cost = 3
    b_cost = 1
    ax, ay = a_xy
    bx, by = b_xy
    px, py = p_xy

    if px < 0 or py < 0:
        return -1

    if px - bx == 0 and py - by == 0:
        return b_cost
    if px - ax == 0 and py - ay == 0:
        return a_cost

    a_branch = solve_game(a_xy, b_xy, (px - ax, py - ay))
    b_branch = solve_game(a_xy, b_xy, (px - bx, py - by))
    if a_branch == -1 and b_branch == -1:
        return -1
    if a_branch == -1:
        return b_cost + b_branch
    if b_branch == -1:
        return a_cost + a_branch
    return min(a_branch + a_cost, b_branch + b_cost)


def solve_with_offset(input, offset=0):
    games = parse_input(input)

    tokens_spent = 0
    for game in games:
        a_xy, b_xy, (rpx, rpy) = game
        px = offset + rpx
        py = offset + rpy
        cost = solve_game(a_xy, b_xy, (px, py))
        tokens_spent += cost if cost > 0 else 0
    return tokens_spent


def part1(input):
    return solve(input)


def part2(input):
    # offset = 10000000000000
    offset = 10000
    return solve_with_offset(input, offset)

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 480, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 35574, "Got: {}".format(ans) # 31300 < ans < 35708 (mistake: not checking x // bx == y // by)
        print(f'Pt1::ans: {ans}')

    with timer():
        ans = solve_with_offset(example)
        assert ans == 480, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    # with timer():
    #     ans = part2(example)
        # assert ans == None, "Got: {}".format(ans)
        # print(f'Pt2(example)::ans: {ans}')

    # with timer():
    #     ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
