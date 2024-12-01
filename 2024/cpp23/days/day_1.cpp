#include <string>
#include <algorithm>
#include <cmath>
#include <utils/misc.hpp>

std::string example(R"(3   4
4   3
2   5
1   3
3   9
3   3)");

//std::pair<std::vector<int>, std::vector<int>> parse_input_slow(const std::string &input) {
//    auto lines = Utils::view_lines(input);
//    std::vector<int> list_a{};
//    list_a.reserve(lines.size());
//    std::vector<int> list_b{};
//    list_b.reserve(lines.size());
//    for (const auto &line: lines) {
//        auto pair = Utils::split_regex(std::string(line), R"(\s+)");
//        list_a.push_back(std::stoi(pair.at(0)));
//        list_b.push_back(std::stoi(pair.at(1)));
//    }
//    return {list_a, list_b};
//}

std::pair<std::vector<int>, std::vector<int>> parse_input(const std::string &input) {
    std::vector<int> list_a{};
    std::vector<int> list_b{};

    size_t sol = 0;
    while (sol < input.length()) {
        size_t eol = input.find('\n', sol);
        if (eol == std::string::npos) eol = input.length();

        size_t space = input.find(' ', sol);

        list_a.push_back(std::stoi(input.substr(sol, space - sol)));
        list_b.push_back(std::stoi(input.substr(space, eol - space)));

        sol = eol + 1;
    }

    return {list_a, list_b};
}

void part_i(const std::string &input) {
    auto [list_a, list_b] = parse_input(input);
    std::sort(list_a.begin(), list_a.end());
    std::sort(list_b.begin(), list_b.end());
    int ans = 0;
    for (int i = 0; i < list_a.size(); ++i) {
        ans += std::abs(list_a.at(i) - list_b.at(i));
    }
    std::println("Part i: {}", ans);
}

std::unordered_map<int, int> freq_count(const std::vector<int> &nums) {
    std::unordered_map<int, int> freqs{};
    for (const auto &num: nums) {
        freqs[num]++;
    }
    return freqs;
}

void part_ii(const std::string &input) {
    auto [list_a, list_b] = parse_input(input);
    auto b_freqs = freq_count(list_b);

    int ans = 0;
    for (const auto &id: list_a) {
        ans += b_freqs[id] * id;
    }
    std::println("Part ii: {}", ans);
}

int main() {
    auto input = Utils::read_input(1);
    Utils::with_timer("Parse input",  [&input](){ parse_input(input); });

    Utils::with_timer("Part i (example)", []() {
        part_i(example);
    });

    Utils::with_timer("Part i (input)", [&input]() {
        part_i(input);
    });

    Utils::with_timer("Part ii (example)", []() {
        part_ii(example);
    });

    Utils::with_timer("Part ii (input)", [&input]() {
        part_ii(input);
    });
}