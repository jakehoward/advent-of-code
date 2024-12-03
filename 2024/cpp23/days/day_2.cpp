#include <string>
#include <vector>
#include <algorithm>
#include <cmath>
#include <utils/misc.hpp>

std::string example(R"(7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9)");

std::vector<std::vector<int>> parse(const std::string &input) {
    auto lines = Utils::split_lines(input);
    std::vector<std::vector<int>> output{};
    output.reserve(lines.size());
    for (const auto &line: lines) {
        auto tokens = Utils::split_whitespace(line);
        std::vector<int> line_nums(tokens.size());
        std::transform(tokens.begin(), tokens.end(), line_nums.begin(), [](auto &t) { return std::stoi(t); });
        output.push_back(line_nums);
    }
    return output;
}

void part_i(const std::string &input) {
    auto lines = parse(input);
    int num_safe = 0;
    for(const auto &line: lines) {
        std::vector<int> differences(line.size() - 1);
        for (int i = 0; i < line.size() - 1; ++i) {
            differences[i] = abs(line[i] - line[i+1]);
        }

        auto sorted = std::is_sorted(line.cbegin(), line.cend()) || std::is_sorted(line.crbegin(), line.crend());
        auto all_diffs_ok = std::all_of(differences.cbegin(), differences.cend(), [](const auto &diff) { return 0 < diff && diff <= 3;});
        auto is_safe = sorted && all_diffs_ok;

//        std::println("{} {} {} {} {}", line,differences, sorted, all_diffs_ok, is_safe);
        if (is_safe) {
            num_safe += 1;
        }
    }
    std::println("Part i: {}", num_safe);
}

void part_ii(const std::string &input) {
    std::println("Part ii: {}", "Not implemented");
}

int main() {
    auto input = Utils::read_input(2);
    Utils::with_timer("Parse input",  [&input](){ parse(input); });

    Utils::with_timer("Part i (example)", []() {
        part_i(example);
    });

    Utils::with_timer("Part i (input)", [&input]() {
        part_i(input);
    });

//    Utils::with_timer("Part ii (example)", []() {
//        part_ii(example);
//    });

//    Utils::with_timer("Part ii (input)", [&input]() {
//        part_ii(input);
//    });
}