#include <print>
#include <algorithm>

#include <utils/read.hpp>

/* You can only move Up/Down/Left/Right
 *
 * File at ./input/day_0.txt
 *
 * Starting at the only opening at the top of the woods,
 * how many steps does it take to get to the bottom?
 *
 * Ans: 6 (D, R, D, D, L, D)
 */

//** **
//**  *
//*** *
//**  *
//** **

int main() {
    auto lines = Utils::read_input("day_0");
    std::for_each(lines.cbegin(), lines.cend(), [](const auto &line) { std::println("{}",line); });
}