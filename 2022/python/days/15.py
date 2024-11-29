from pathlib import Path
import re

from utils.misc import timer
from utils.read import read_input

example = """Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3"""

def parse_input(input):
    sensor_beacon_pairs = []
    for line in input.splitlines():
        m = re.search('^Sensor at x=(\\d+), y=(\\d+): closest beacon is at x=(\\-?\\d+), y=(\\d+)$', line)
        sx, sy, bx, by = m.groups()
        sensor_beacon_pairs.append(((int(sx), int(sy)), (int(bx), int(by))))
    return sensor_beacon_pairs

def part1(input):
    answer = '...'
    print(f'Pt1::ans: {answer}')

def part2(input):
    answer = '...'
    print(f'Pt2::ans: {answer}')

def run():
    day = Path(__file__).name.split('.')[0]
    input = read_input(day)
    with timer():
        part1(example)

    # with timer():
    #     part1(input)

    # with timer():
    #     part2(example)

    # with timer():
    #     part2(input)

if __name__ == "__main__":
    run()