#include "index.h"
#include "utils.h"
#include "matrix.h"
#include <set>
#include <iterator>
#include <algorithm>

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

    int count_arrangements(const PuzzleLine &puzzleLine) {
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

        int ans{0};
        auto all_springs_regex = std::regex("[\?#]+");

        // ex: .??..??...?##. 1,1,3
        // if ? => can first num fit? yes: drop num, and consume num + 1 and any (.) from config, recurse; no: consume to next (.) or (?#) and recurse
        if (config.at(0) == '?' || config.at(0) == '#') {
            const long n = nums.at(0);
            bool num_can_fit = (config.length() == n && regex_match(config.substr(0, n), all_springs_regex)) ||
                               (config.length() > n && regex_match(config.substr(0, n), all_springs_regex) &&
                                       ((config.at(n + 1) == '?') || (config.at(n + 1) == '.')));
            if (num_can_fit) {
                std::string next_config;
                if (config.length() == n) {
                    next_config = "";
                } else if (config.length() == n + 1) {
                    next_config = ".";
                } else {
                    next_config = "." + config.substr(n+1, config.length() - n );
                }

                auto next_nums = nums;
                next_nums.erase(next_nums.begin());
                ans += count_arrangements({next_config, next_nums});
            } else if (config.at(0) == '#') {
                // we have to match the num or the pattern is invalid and we can't continue
                return 0;
            }
        } else if (config.at(0) == '#') {
            return 0;
        }
        // if # => can first num fit? yes: drop num, and consume num + 1 and any (.) from config, recurse: no: return 0

        // else: drop 1, consume all dots from config and recurse (todo: actually consume all dots)
        //       (in case the first char is ?, we also take the path where we don't fit the number this time, even if we can)
        ans += count_arrangements({ config.substr(1, config.length()-1), nums});

        return ans;
    }

    void part_ii(std::string const &input) {
        std::cout << "The answer is: " << "TBD!" << std::endl;
    }

    void part_i(std::string const &input) {
//        auto parsed = parse_input(input);
//        long ans{0};
//        for(const PuzzleLine &puzzle : parsed) {
//            ans += count_arrangements(puzzle);
//        }
        auto ans = count_arrangements({"??.??.##?", {1,1,3}});
//        auto ans = count_arrangements({"?.", {}});
//        auto ans = count_arrangements({"?.", {}});
        std::cout << "The answer is: " << ans << std::endl;
    }
}

void Days::run12(Utils::Mode mode, int part) {
    std::string input;
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