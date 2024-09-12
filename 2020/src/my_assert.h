#pragma once
#include <iostream>
#include <cassert>

#define my_assert(condition, message) \
    if (!(condition)) { \
        std::cerr << "Assertion failed: (" #condition "), message: " << message << "\n"; \
        assert(condition); \
    }
