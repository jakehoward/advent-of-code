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
    Utils::with_timer([]() {
        auto lines = Utils::read_input(0);

        part_i(example);
//        part_i(lines);
//
//        part_ii(example);
//        part_ii(lines);
    });
}