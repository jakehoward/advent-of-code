#pragma once

#include <string>
#include <vector>
#include <regex>

#include <iostream>
#include <fstream>
#include <sstream>

#include <exception>

#define my_assert(condition, message) \
    if (!(condition)) { \
        std::cerr << "Assertion failed: (" #condition "), message: " << message << "\n"; \
        assert(condition); \
    }

namespace Utils {
    std::string readFile(std::string const& path);
    std::vector<std::string> split(const std::string& str, std::string delimiter);
    std::vector<std::string> split(std::string const& str, std::regex const& re);
}