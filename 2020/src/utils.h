#pragma once

#include <string>
#include <vector>
#include <map>
#include <regex>

#include <utility>

#include <iostream>
#include <fstream>
#include <sstream>

#include <exception>

#define my_assert(condition, message) \
    if (!(condition)) { \
        std::cerr << "Assertion failed: (" #condition "), message: " << message << "\n"; \
        assert(condition); \
    }

template<typename T>
std::ostream &operator<<(std::ostream &os, const std::vector<T> &vec) {
    os << "[";
    for (int i = 0; i < vec.size(); ++i) {
        os << vec[i];
        if (i < vec.size() - 1) {
            os << ", ";
        }
    }
    os << "]";
    return os;
}

template<typename Key, typename Value>
std::ostream &operator<<(std::ostream &os, const std::map<Key, Value> &m) {
    os << "{\n";
    for (auto [k, v]: m) {
        os << "  " << k << ":" <<  v << ",\n";
    }
    os << "}";
    return os;
}

namespace Utils {
    enum class Mode {
        Example, Input
    };

    std::string readFile(std::string const &path);

    std::vector<std::string> readLines(std::string const &path);

    std::vector<std::string> split(std::string const &str, std::string const &delimiter);

    std::vector<std::string> splitLines(std::string const &str);

    std::vector<std::string> splitWhitespace(std::string const &str);

    std::vector<std::string> split(std::string const &str, std::regex const &re);

    std::vector<long> stringsToLongs(std::vector<std::string> const& strings);

    Mode parseMode(std::string const &modeStr);
}