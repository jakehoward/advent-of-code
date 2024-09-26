#include "index.h"
#include "utils.h"
#include "matrix.h"
#include <set>
#include <iterator>
#include <algorithm>
#include <boost/algorithm/string/join.hpp>

namespace Day21 {
    std::string example = R"(...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........)";


    void part_ii(std::string const &input) {
        std::cout << "The answer is: " << "TBD!" << std::endl;
    }

    void part_i(std::string const &input) {
        std::vector<std::string> words = {"Boost", "is", "working", "!"};
        std::string result = boost::algorithm::join(words, " ");
        std::cout << "Boost test result: " << result << std::endl;
        std::cout << "The answer is: " << "TBD!" << std::endl;
    }
}

void Days::run21(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = Day21::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/21.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return Day21::part_i(input);
    }
    if (part == 2) {
        return Day21::part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}