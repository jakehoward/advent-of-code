#include "index.h"
#include "utils.h"
#include "matrix.h"
#include <set>
#include <iterator>
#include <algorithm>
#include <map>

using std::vector;
using std::string;
using std::regex_match;

namespace Day12 {
    string example = R"(???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1)";

    struct PuzzleLine {
        string config;
        vector<long> nums;
    };

    vector<PuzzleLine> parse_input(const string &raw) {
        auto lines = Utils::splitLines(raw);
        vector<PuzzleLine> parsed{};
        parsed.reserve(lines.size());
        for (const auto &line: lines) {
            auto tokens = Utils::splitWhitespace(line);
            auto config = tokens[0];
            auto nums = Utils::stringsToLongs(Utils::split(tokens[1], std::regex(R"(,)")));
            parsed.emplace_back(PuzzleLine{config, nums});
        }
        return parsed;
    }

    string puzzle_to_string(const PuzzleLine & p) {
        string nums;
        for (const auto n: p.nums) {
            nums += std::to_string(n);
        }
        return p.config + nums;
    }

    // Cleared on each top level run (todo: can I add a struct of string and vector to a map, how is equality done?)
    std::map<string, long> cache{};
    long count_arrangements(const PuzzleLine &puzzleLine) {
        const string pAsString = puzzle_to_string(puzzleLine);
        if (cache.contains(pAsString)) {
            return cache.at(pAsString);
        }
        const auto nums = puzzleLine.nums;
        const auto config = puzzleLine.config;
        if (config.empty()) {
            if (!nums.empty()) {
                return 0;
            }
            return 1;
        }
        if (nums.empty()) {
            if (regex_match(config, std::regex(".*#.*"))) {
                return 0;
            }
            return 1;
        }

        long ans{0};
        auto all_springs_regex = std::regex("[\?#]+");

        // ex: .??..??...?##. 1,1,3
        // if ? => can first num fit? yes: drop num, and consume num + 1 and any (.) from config, recurse; no: consume to next (.) or (?#) and recurse
        const long n = nums.at(0);
        bool all_springs = regex_match(config.substr(0, n), all_springs_regex);
        bool num_can_fit = (config.length() == n && all_springs) ||
                           (config.length() > n && all_springs &&
                            ((config.at(n) == '?') || (config.at(n) == '.')));

        std::string next_config;
        vector<long> next_nums{};
        if (num_can_fit) {
            if (config.length() == n) {
                next_config = "";
            } else if (config.length() == n + 1) {
                next_config = ".";
            } else {
                next_config = "." + config.substr(n + 1, config.length() - n);
            }

            next_nums = nums;
            next_nums.erase(next_nums.begin());
        }

        if (config.at(0) == '.') {
            ans += count_arrangements({config.substr(1, config.length() - 1), nums});
        } else if (config.at(0) == '?') {
            if (num_can_fit) {
                ans += count_arrangements({next_config, next_nums});
            }
            ans += count_arrangements({config.substr(1, config.length() - 1), nums});
        } else if (config.at(0) == '#') {
            if (num_can_fit) {
                ans += count_arrangements({next_config, next_nums});
            } else {
                return 0;
            }
        }

        cache.insert({pAsString, ans});
        return ans;
    }

    void part_ii(std::string const &input) {
        auto parsed = parse_input(input);
        int num_copies = 5;
        long ans{0};
        for (const PuzzleLine &puzzle: parsed) {
            auto biggerConfigVec = vector(num_copies, puzzle.config);
            string biggerConfig;
            bool first = true;
            for (const auto &config: biggerConfigVec) {
                if (first) {
                    first = false;
                    biggerConfig += config;
                    continue;
                }
                biggerConfig += ("?" + config);
            }
            vector<long> biggerNums(num_copies * puzzle.nums.size());
            for (int i = 0; i < num_copies; ++i) {
                for (int n = 0; n < puzzle.nums.size(); ++n) {
                    biggerNums[(i * puzzle.nums.size()) + n] = puzzle.nums.at(n);
                }
            }
            PuzzleLine biggerPuzzle = {biggerConfig, biggerNums};
            cache.clear();
            ans += count_arrangements(biggerPuzzle);
        }
        std::cout << "The answer is: " << ans << std::endl;
    }

    void part_i(std::string const &input) {
        auto parsed = parse_input(input);
        long ans{0};
        for (const PuzzleLine &puzzle: parsed) {
            ans += count_arrangements(puzzle);
        }
//        auto ans = count_arrangements({"??.??.##?", {1,1,3}});
        std::cout << "The answer is: " << ans << std::endl;
    }
}

void Days::run12(Utils::Mode mode, int part) {
    std::string input;
    Day12::cache.clear();
    switch (mode) {
        case Utils::Mode::Example:
            input = Day12::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/12.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return Day12::part_i(input);
    }
    if (part == 2) {
        return Day12::part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}