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

    long count_trees(Matrix<char, long> const & m, int dx, int dy) {
        int latch = 0;
        long tree_count = 0;
        int x = 0;
        int y = 0;
        while(latch < 10'000) {
            ++latch;
            if (m.at(x, y) == '#') {
                ++tree_count;
            }
            y += dy;
            x += dx;
            if (!m.inBounds(x, y)) {
                break;
            }
        }
        return tree_count;
    }

    void part_ii(std::string const &input) {
        auto m = Utils::buildMatrix(input, {true});
        auto a = count_trees(m, 1, 1); // Right 1, down 1.
        auto b = count_trees(m, 3, 1); // Right 3, down 1. (This is the slope you already checked.)
        auto c = count_trees(m, 5, 1); // Right 5, down 1.
        auto d = count_trees(m, 7, 1); // Right 7, down 1.
        auto e = count_trees(m, 1, 2); // Right 1, down 2.
        auto ans = a * b * c * d * e;
        std::cout << "The answer is: " << ans << std::endl;
    }

    void part_i(std::string const &input) {
        auto m = Utils::buildMatrix(input, {true});
        auto tree_count = count_trees(m, 3, 1);
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