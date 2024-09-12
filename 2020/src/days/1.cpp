#include "index.h"
#include "utils.h"

std::string example = R"(1721
979
366
299
675
1456)";

void Days::run1() {
    auto tokens = Utils::split(example, std::regex(R"(\n)"));
    for (auto token: tokens) {
        std::cerr << token << std::endl;
    }
    std::cout << "The answer is: " << "TBD!" << std::endl;
}