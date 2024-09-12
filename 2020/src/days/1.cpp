#include "index.h"
#include "utils.h"

std::string example = R"(1721
979
366
299
675
1456)";

void answer(std::string const& input) {
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

void Days::run1(Utils::Mode mode) {
    switch (mode) {
        case Utils::Mode::Example:
            answer(example);
            break;
        case Utils::Mode::Input:
            answer(Utils::readFile("./input/1.txt"));
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
}