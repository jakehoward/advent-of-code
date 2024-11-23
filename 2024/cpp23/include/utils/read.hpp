#pragma once

#include <vector>
#include <string>
#include <print>
#include <fstream>
#include <format>
#include <exception>

namespace Utils {
    std::vector<std::string> read_input(const std::string &day_name) {
        auto filename = std::format("../input/{}.txt", day_name);

        std::vector<std::string> lines{};

        std::ifstream f(filename);
        if (f.is_open()) {
            std::string line;
            while (f) {
                std::getline(f, line);
                lines.push_back(line);
            }
            return lines;
        }

        throw std::runtime_error(std::format("Could not open file: {}", filename));
    }
}