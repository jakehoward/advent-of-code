#include <string>
#include <utils/misc.hpp>

std::string example(R"(7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9)");

void part_i(const std::string &input) {
    int num_safe = 0;
    auto safe = true;
    int pos = 0;
    while (pos < input.length()) {
        auto end = input.find_first_of(" \n", pos);
        
    }
    std::println("Part i: {}", "Not implemented");
}

void part_ii(const std::string &input) {
    std::println("Part ii: {}", "Not implemented");
}

int main() {
    auto input = Utils::read_input(2);
//    Utils::with_timer("Parse input",  [&input](){ parse_input(input); });

    Utils::with_timer("Part i (example)", []() {
        part_i(example);
    });

//    Utils::with_timer("Part i (input)", [&input]() {
//        part_i(input);
//    });

//    Utils::with_timer("Part ii (example)", []() {
//        part_ii(example);
//    });

//    Utils::with_timer("Part ii (input)", [&input]() {
//        part_ii(input);
//    });
}