#include <string>
#include <utils/misc.hpp>

std::string example(R"(** *
** *)");

void part_i(const std::string &input) {
    std::println("{}", "Part i - Not implemented");
}

void part_ii(const std::string &input) {
    std::println("{}", "Part ii - Not implemented");
}

int main() {
    auto input = Utils::read_input(0);

    Utils::with_timer([]() {
        part_i(example);
    });

//    Utils::with_timer([]() {
//        part_i(input);
//    });

//    Utils::with_timer([]() {
//        part_ii(example);
//    });

//    Utils::with_timer([]() {
//        part_ii(input);
//    });
}