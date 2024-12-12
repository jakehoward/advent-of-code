from pathlib import Path

from utils.grid import make_grid
from utils.misc import timer
from utils.read import read_input

example = """
AAAA
BBCD
BBCC
EEEC
""".strip()

example_2 = """
OOOOO
OXOXO
OOOOO
OXOXO
OOOOO
""".strip()

example_3 = """
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
""".strip()


def calculate_price(grid, point):
    region_id = grid.at_p(point)
    region_points = set()
    points_to_search = {point}
    perimeter = 0
    while points_to_search:
        p = points_to_search.pop()
        if grid.at_p(p) == region_id:
            region_points.add(p)
            nbr_xys = grid.get_nbr_xys(p[0], p[1])
            perimeter += (len([p for p in nbr_xys if grid.at_p(p) != region_id]) + (4 - len(nbr_xys)))
            points_to_search = points_to_search.union({p for p in nbr_xys if p not in region_points})
    return {'price': len(region_points) * perimeter, 'points': region_points}


def part1(input):
    grid = make_grid(input)

    seen_region_points = set()
    price = 0
    for point, value in grid:
        if point in seen_region_points: continue
        seen_region_points.add(point)

        result = calculate_price(grid, point)
        price += result['price']
        seen_region_points = seen_region_points.union(result['points'])

    answer = price
    return answer


def part2(input):
    answer = '...'
    return answer


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        ans = part1(example)
        assert ans == 140, "Got: {}".format(ans)
        print(f'Pt1(example)::ans: {ans}')

    with timer():
        ans = part1(example_2)
        assert ans == 772, "Got: {}".format(ans)
        print(f'Pt1(example_2)::ans: {ans}')

    with timer():
        ans = part1(example_3)
        assert ans == 1930, "Got: {}".format(ans)
        print(f'Pt1(example_3)::ans: {ans}')

    with timer():
        ans = part1(input)
        assert ans == 1304764, "Got: {}".format(ans)
        print(f'Pt1::ans: {ans}')

    # with timer():
    #     ans = part2(example)
    #     assert ans == 80, "Got: {}".format(ans)
    #     print(f'Pt2(example)::ans: {ans}')

    # with timer():
    #     ans = part2(input)
    #     assert ans == None, "Got: {}".format(ans)
    #     print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
