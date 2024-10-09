#include <catch2/catch_test_macros.hpp>
#include "../src/days/17.h"

TEST_CASE("Day 17 wiring test", "[day17]") {
    REQUIRE(Day17::test_test() == 12);
}