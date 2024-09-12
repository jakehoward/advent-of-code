#include "index.h"
#include "utils.h"
#include "matrix.h"

namespace Day3 {
    std::string example = R"(..##.......
#...#...#..
.#....#..#.
..#.#...#.#
.#...##..#.
..#.##.....
.#.#.#....#
.#........#
#.##...#...
#...##....#
.#..#...#.#)";

    void part_ii(std::string const &input) {
        std::cout << "The answer is: " << "TBD!" << std::endl;
    }

    void part_i(std::string const &input) {
        auto m = Utils::buildMatrix(input, {true});
        int latch = 0;
        int tree_count = 0;
        int x = 0;
        int y = 0;
        while(latch < 10'000) {
            ++latch;
            if (m.at(x, y) == '#') {
                ++tree_count;
            }
            x += 3;
            y += 1;
            if (!m.inBounds(x, y)) {
                break;
            }
        }
        std::cout << "The answer is: " << tree_count << std::endl;
    }
}

void Days::run3(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = Day3::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/3.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return Day3::part_i(input);
    }
    if (part == 2) {
        return Day3::part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}