/* You can only move Up/Down/Left/Right
 * Part i:
 * File at ./input/day_0.txt
 *
 * Starting at the only opening at the top of the woods,
 * how many steps does it take to get to the bottom?
 *
 * Ans: 6 (D, R, D, D, L, D)
 */



#include <string>
#include <utils/misc.hpp>

std::string example(R"(
** **
**  *
*** *
**  *
** **)");

void part_i(const std::string &input) {
    std::println("{}", "Part i - Not implemented");
}

void part_ii(const std::string &input) {
    std::println("{}", "Part ii - Not implemented");
}

int main() {
    Utils::with_timer([]() {
        auto lines = Utils::read_input(0);

        part_i(example);
//        part_i(lines);
//
//        part_ii(example);
//        part_ii(lines);
    });
}