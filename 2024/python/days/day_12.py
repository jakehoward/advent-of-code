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

example_4 = """
EEEEE
EXXXX
EEEEE
EXXXX
EEEEE
""".strip()

example_5 = """
AAAAAA
AAABBA
AAABBA
ABBAAA
ABBAAA
AAAAAA
""".strip()

def calculate_num_sides(region_points, grid=None):
    xs = [p[0] for p in region_points]
    ys = [p[1] for p in region_points]
    min_x, max_x = min(xs), max(xs)
    min_y, max_y = min(ys), max(ys)

    y_range = range(min_y -1, max_y + 2)
    x_range = range(min_x -1, max_x + 2)

    horizontal_edges = 0
    for (top, bottom) in zip(y_range, y_range[1:]):
        tracking_top = False
        tracking_bottom = False
        for x in range(min_x, max_x + 1):
            if (x, top) in region_points and (x, bottom) not in region_points:
                if not tracking_top:
                    tracking_top = True
                    tracking_bottom = False
                    horizontal_edges += 1
            elif (x, bottom) in region_points and (x, top) not in region_points:
                if not tracking_bottom:
                    tracking_bottom = True
                    tracking_top = False
                    horizontal_edges += 1
            else:
                if tracking_top or tracking_bottom:
                    tracking_top = False
                    tracking_bottom = False

    vertical_edges = 0
    for (left, right) in zip(x_range, x_range[1:]):
        tracking_left = False
        tracking_right = False
        for y in range(min_y, max_y + 1):
            if (left, y) in region_points and (right, y) not in region_points:
                if not tracking_left:
                    tracking_left = True
                    tracking_right = False
                    vertical_edges += 1
            elif (right, y) in region_points and (left, y) not in region_points:
                if not tracking_right:
                    tracking_right = True
                    tracking_left = False
                    vertical_edges += 1
            else:
                if tracking_left or tracking_right:
                    tracking_left = False
                    tracking_right = False

    # if grid: print(grid.at_p(list(region_points)[0]) + ': h:', horizontal_edges, '+ v:', vertical_edges, '=', horizontal_edges + vertical_edges)
    return horizontal_edges + vertical_edges


def calculate_price(grid, point, bulk_discount):
    region_id = grid.at(point)
    region_points = set()
    points_to_search = {point}
    perimeter = 0

    while points_to_search:
        p = points_to_search.pop()
        if grid.at(p) == region_id:
            region_points.add(p)
            nbr_xys = grid.get_nbr_xys(p[0], p[1])
            perimeter += (len([p for p in nbr_xys if grid.at(p) != region_id]) + (4 - len(nbr_xys)))
            points_to_search = points_to_search.union({p for p in nbr_xys if p not in region_points})
    num_sides = calculate_num_sides(region_points, grid) if bulk_discount else -1
    return {'price': len(region_points) * num_sides if bulk_discount else len(region_points) * perimeter,
            'points': region_points}


def get_total_price(input, bulk_discount=False):
    grid = make_grid(input)

    seen_region_points = set()
    price = 0
    for point, value in grid:
        if point in seen_region_points: continue
        seen_region_points.add(point)

        result = calculate_price(grid, point, bulk_discount)
        price += result['price']
        seen_region_points = seen_region_points.union(result['points'])

    return price


def part1(input):
    answer = get_total_price(input)
    return answer


def part2(input):
    answer = get_total_price(input, True)
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

    with timer():
        ans = part2(example)
        assert ans == 80, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')

    with timer():
        ans = part2(example_2)
        assert ans == 436, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')

    with timer():
        ans = part2(example_4)
        assert ans == 236, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')

    with timer():
        ans = part2(example_5)
        assert ans == 368, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')

    with timer():
        ans = part2(example_3)
        assert ans == 1206, "Got: {}".format(ans)
        print(f'Pt2(example)::ans: {ans}')

    with timer():
        ans = part2(input)
        assert ans == 811148, "Got: {}".format(ans)
        print(f'Pt2::ans: {ans}')


if __name__ == "__main__":
    run()
