#include "index.h"
#include "utils.h"

std::string example = R"(1721
979
366
299
675
1456)";

void part_ii(std::string const &input) {
    auto nums = Utils::stringsToLongs(Utils::splitLines(input));
    long a, b, c;
    for (auto m: nums) {
        for (auto n: nums) {
            for (auto o: nums) {
                if (n + m + o == 2020) {
                    a = m;
                    b = n;
                    c = o;
                }
            }
        }
    }

    std::cout << "The answer is: " << a * b * c << std::endl;
}

void part_i(std::string const &input) {
    auto nums = Utils::stringsToLongs(Utils::splitLines(input));
    long a, b;
    for (auto n: nums) {
        for (auto m: nums) {
            if (n + m == 2020) {
                a = n;
                b = m;
            }
        }
    }

    std::cout << "The answer is: " << a * b << std::endl;
}

void Days::run1(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/1.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return part_i(input);
    }
    if (part == 2) {
        return part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}