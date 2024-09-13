#include "index.h"
#include "utils.h"
#include "matrix.h"
#include <set>
#include <iterator>
#include <algorithm>

/*
byr (Birth Year)
iyr (Issue Year)
eyr (Expiration Year)
hgt (Height)
hcl (Hair Color)
ecl (Eye Color)
pid (Passport ID)
cid (Country ID)
 */

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

    struct PassportField {
        std::string name;
        std::string value;
    };

    std::vector<PassportField> parse_passport(std::string const &passport) {
        auto raw_fields = Utils::split(passport, std::regex(R"(\s+|\n)"));
        std::vector<PassportField> fields{};
        for (auto const &field: raw_fields) {
            auto name_value = Utils::split(field, ":");
            fields.push_back({name_value.at(0), name_value.at(1)});
        }
        return fields;
    }


    void part_i(std::string const &input) {
        std::set<std::string> required_fields{"byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"};
        auto raw_passports = Utils::split(input, std::regex(R"(\n\n)"));
        std::vector<std::vector<PassportField>> passports{};
        for (auto const &raw_passport: raw_passports) {
            passports.push_back(parse_passport(raw_passport));
        }

        int num_valid = 0;
        for (auto const &passport: passports) {
            std::vector<std::string> field_names_vec(passport.size());
            std::transform(passport.begin(), passport.end(), field_names_vec.begin(),
                           [](PassportField const &f) { return f.name; });

            std::set<std::string> field_names(field_names_vec.begin(), field_names_vec.end());
//            std::cerr << std::endl << std::vector(required_fields.begin(), required_fields.end()) << std::endl << std::vector(field_names.begin(), field_names.end()) << std::endl;
            std::vector<std::string> intersection{};
            std::set_intersection(required_fields.begin(), required_fields.end(), field_names.begin(), field_names.end(), std::back_inserter(intersection));
            if (intersection.size() == required_fields.size()) {
                ++num_valid;
            }
        }
        std::cout << "The answer is: " << num_valid << std::endl;
    }
}

void Days::run4(Utils::Mode mode, int part) {
    std::string input;
    switch (mode) {
        case Utils::Mode::Example:
            input = Day4::example;
            break;
        case Utils::Mode::Input:
            input = Utils::readFile("./input/4.txt");
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