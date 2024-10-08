#include "index.h"
#include "utils.h"
#include "matrix.h"
#include <set>
#include <iterator>
#include <ranges>
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
        std::vector<std::string> otherWords = {"Boost", "is", "working", "!"};

        std::cout << "Words: Deep equal? " << ((words == otherWords) ? "true" : "false") << std::endl;

        std::vector<std::map<int, int>> foos {{{1, 2}, {3, 4}}};
        std::vector<std::map<int, int>> otherFoos {{{1, 2}, {3, 4}}};
        std::cout << "Foos: Deep equal? " << ((foos == otherFoos) ? "true" : "false") << std::endl;

        std::vector<int> nums {1,2,3,4,5,6,7,8,9,10,11,12};
        auto v = nums
                | std::views::reverse
                | std::views::drop(2)
                | std::views::transform([](const int &a){ return a * a; })
                | std::views::filter([](const int &a){ return a % 2 == 0; })
                | std::views::take(3)
                | std::views::take_while([](const int &a){ return a > 50; })
                | std::ranges::to<std::vector>();
        std::cout << "Vector view stuff (before): " << nums << std::endl;
        std::cout << "Vector view stuff (after) : " << v << std::endl;

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