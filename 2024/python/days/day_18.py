from pathlib import Path
from heapq import heappush, heappop

from utils.grid import make_grid_with_points
from utils.misc import timer
from utils.read import read_input

example = """
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0""".strip()

# Ex: 7x7 (12 bytes)
# Full: 71x71 (0 -> 70 incl)

def parse(input):
    return [(int(x.split(',')[0]), int(x.split(',')[1])) for x in input.splitlines()]

def shortest_path(grid, start, end):
    Q = []
    seen = set()

    heappush(Q, (0, start))
    while Q:
        cost, pos = heappop(Q)
        if pos == end:
            return cost
        if pos in seen:
            continue
        seen.add(pos)

        px, py = pos
        nbrs = [n for n in grid.get_nbr_xys(px, py) if grid.at(n) != '#']
        for n in nbrs:
            heappush(Q, (cost + 1, n))



def part1(input, size, num_bytes):
    start = (0, 0)
    end = (size-1, size-1)
    # num_bytes = 12 # 1024
    # size = 7 # 71
    byte_positions = parse(input)
    grid = make_grid_with_points({'#': set(byte_positions[0:num_bytes]) }, size, size, '.')
    print(grid.as_string())
    return shortest_path(grid, start, end)

def part2(input):
    start = (0, 0)
    end = (70, 70)
    # num_bytes = 12 # 1024
    size = 71
    byte_positions = parse(input)
    for num_bytes in range(1, len(byte_positions)):
        grid = make_grid_with_points({'#': set(byte_positions[0:num_bytes])}, size, size, '.')
        if shortest_path(grid, start, end) is None:
            return byte_positions[0:num_bytes][-1]

def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example, 7, 12)
        assert ans == 22, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')
        ans = None

    with timer():
        ans = part1(input, 71, 1024)
        # assert ans == None, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')
        ans = None

    # with timer():
    #     ans = part2(example)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')
    #     ans = None

    with timer():
        ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')
        ans = None


if __name__ == "__main__":
    run()