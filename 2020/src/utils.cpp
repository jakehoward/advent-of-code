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

std::vector<std::string> Utils::split(const std::string& str, std::string delimiter) {
    std::vector<std::string> result;
    std::stringstream stream(str);

    std::string token;
    while (std::getline(stream, token, delimiter[0])) {
        result.push_back(token);
    }

    return result;
}

std::vector<std::string> Utils::split(std::string const& str, std::regex const& re) {
    std::sregex_token_iterator it(str.begin(), str.end(), re, -1);
    return {it, std::sregex_token_iterator()};
}