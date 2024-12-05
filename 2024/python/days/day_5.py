from pathlib import Path

from utils.misc import timer
from utils.read import read_input

example = """47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47"""


def parse_input(input):
    order_lines, page_lines = input.split('\n\n')
    orders = [(int(a), int(b)) for a, b in [line.split('|') for line in order_lines.splitlines()]]
    pages = [list(map(int, page)) for page in [pages.split(',') for pages in page_lines.splitlines()]]
    return orders, pages


def is_ordered(orders, page):
    page_idx = {page: idx for idx, page in enumerate(page)}
    for lt, gt in orders:
        if lt in page_idx and gt in page_idx:
            if page_idx.get(lt) > page_idx.get(gt):
                return False
    return True


def part1(input):
    orders, pages = parse_input(input)
    middle_nums = []
    for page in pages:
        ordered = is_ordered(orders, page)
        if ordered:
            middle_nums.append(page[len(page) // 2])
    answer = sum(middle_nums)
    print(f'Pt1::ans: {answer}')

DO_CHECKS = True
def part2(input):
    orders, pages = parse_input(input)
    middle_nums = []
    for page in pages:
        page_idx = {page: idx for idx, page in enumerate(page)}
        ordered = is_ordered(orders, page)
        if not ordered:
            ordered_page = page[:]
            while not is_ordered(orders, ordered_page):
                for lt, gt in orders:
                    if lt in page_idx and gt in page_idx:
                        if ordered_page.index(lt) > ordered_page.index(gt):
                            ordered_page.pop(ordered_page.index(lt))
                            ordered_page.insert(ordered_page.index(gt), lt)
            if DO_CHECKS:
                assert len(ordered_page) == len(
                    page), f"Ordered page has {len(ordered_page)} elements, but page has {len(page)} elements"
                assert is_ordered(orders, ordered_page), f"Page is not ordered {ordered_page}"
            middle_nums.append(ordered_page[len(ordered_page) // 2])
    answer = sum(middle_nums)
    print(f'Pt2::ans: {answer}')


def run():
    day = Path(__file__).name.split('.')[0].split('_')[-1]
    input = read_input(day)
    with timer():
        part1(example)

    with timer():
        part1(input)

    with timer():
        part2(example)

    with timer():
        part2(input)


if __name__ == "__main__":
    run()
