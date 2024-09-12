#include <utility>
#include "utils.h"

std::string Utils::readFile(std::string const& path) {
    std::ifstream file(path);
    if (!file) {
        throw std::runtime_error("Failed to open " + path);
    }

    std::ostringstream oss;
    oss << file.rdbuf();

    return oss.str();
}

std::vector<std::string> Utils::readLines(std::string const& path) {
    return Utils::split(Utils::readFile(path), "\n");
}

Matrix<char, long> Utils::buildMatrix(std::string const &input, MatrixConfig const& config) {
    auto lines = Utils::splitLines(input);

    // We'll make a lot of assumptions and plough boldy onwards...
    std::vector<char> data{};
    int sz = lines[0].length() * lines.size();
    data.reserve(sz);
    int x = 0;
    for (auto const& line: lines) {
        for (auto c: line) {
            ++x;
            data.push_back(c);
        }
    }

    return std::move(Matrix<char, long>(data, lines[0].length(), lines.size(), config));
}


std::vector<std::string> Utils::split(std::string const& str, std::string const& delimiter) {
    std::vector<std::string> result;
    std::stringstream stream(str);

    std::string token;
    while (std::getline(stream, token, delimiter[0])) {
        result.push_back(token);
    }

    return result;
}

std::vector<std::string> Utils::splitLines(std::string const& str) {
    return Utils::split(str, "\n");
}

std::vector<std::string> Utils::splitWhitespace(std::string const& str) {
    return Utils::split(str, std::regex(R"(\s+)"));
}

std::vector<std::string> Utils::split(std::string const& str, std::regex const& re) {
    std::sregex_token_iterator it(str.begin(), str.end(), re, -1);
    return {it, std::sregex_token_iterator()};
}

std::vector<long> Utils::stringsToLongs(std::vector<std::string> const& strings) {
    std::vector<long> longs(strings.size());
    std::transform(strings.begin(), strings.end(), longs.begin(), [](std::string const& s){ return std::stol(s); });
    return std::move(longs);
}

Utils::Mode Utils::parseMode(std::string const& modeStr) {
    if (modeStr == "example") {
        return Utils::Mode::Example;
    } else if (modeStr == "input") {
        return Utils::Mode::Input;
    } else {
        throw std::runtime_error("No matching mode for: " + modeStr);
    }
}