#include "index.h"
#include "utils.h"
#include "matrix.h"

namespace Day4 {
    std::string example = R"(ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in)";

    void part_ii(std::string const &input) {
        std::cout << "The answer is: " << "TBD!" << std::endl;
    }

    void part_i(std::string const &input) {
        std::cout << "The answer is: " << "TBD!" << std::endl;
    }
}

void Days::run4(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = Day4::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/3.txt");
            break;
        default:
            throw std::runtime_error("No option found for mode");
    }
    if (part == 1) {
        return Day4::part_i(input);
    }
    if (part == 2) {
        return Day4::part_ii(input);
    }

    throw std::runtime_error("No function for part: " + std::to_string(part));
}