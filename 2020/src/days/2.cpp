#include "index.h"
#include "utils.h"

namespace Day2 {
    std::string example = R"(1-3 a: abcde
1-3 b: cdefg
2-9 c: ccccccccc)";

    struct Info {
        long min;
        long max;
        char character;
        std::string password;
    };

    Info parse_line(std::string const &line) {
        auto a = Utils::split(line, std::regex(R"(: )"));
        auto password = a[1];
        auto b = Utils::split(a[0], std::regex(R"(\s+)"));
        auto min_max = b[0];
        auto character = b[1][0];
        auto parsed_min_max = Utils::stringsToLongs(Utils::split(min_max, "-"));
        auto min = parsed_min_max[0];
        auto max = parsed_min_max[1];
//        std::cerr << "min: '" << min << "' max: '" << max << "' c: '" << character << "' password: '" << password << "'" << std::endl;
        return {min, max, character, password};
    }

    bool is_valid(std::string const &line) {
        Info info = parse_line(line);
        int num = 0;
        for (char c: info.password) {
            if(c == info.character) {
                ++num;
            }
        }
        return info.min <= num && num <= info.max;
    }

    void part_ii(std::string const &input) {
        std::cout << "The answer is: " << "TBD" << std::endl;
    }

    void part_i(std::string const &input) {
        auto lines = Utils::splitLines(input);
        int valid_count{0};
        for (auto line: lines) {
            if (is_valid(line)) {
                ++valid_count;
            }
        }
        std::cout << "The answer is: " << valid_count << std::endl;
    }
}

void Days::run2(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = Day2::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/2.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return Day2::part_i(input);
    }
    if (part == 2) {
        return Day2::part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}