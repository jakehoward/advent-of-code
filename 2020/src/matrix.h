#pragma once

#include <vector>
#include "my_assert.h"

struct MatrixConfig {
    bool repeatX;
// Not yet implemented
//    bool repeatNegX;
//    bool repeatY;
//    bool repeatNegY;
};

template<typename Type, typename IndexType>
class Matrix {
public:
    Matrix() {
        throw std::runtime_error("Empty matrix initialisation not allowed");
    }

    Matrix(std::vector<Type> const &data, IndexType x_size, IndexType y_size) {
        Matrix(data, x_size, y_size, {false});
    };

    Matrix(std::vector<Type> const &data, IndexType x_size, IndexType y_size, MatrixConfig config) : data(data),
                                                                                                     y_size(y_size),
                                                                                                     x_size(x_size),
                                                                                                     config(config) {
        if (data.empty()) {
            throw std::runtime_error("Empty Matrix not allowed");
        }

        if (data.size() != y_size * x_size) {
            throw std::runtime_error(
                    std::string("Matrix init mismatch, size: ") +
                    std::to_string(data.size()) +
                    " y_size: " + std::to_string(y_size) +
                    " x_size:" + std::to_string(x_size) +
                    " x*y: " + std::to_string(x_size * y_size));
        }
    };

    Type at(IndexType const x, IndexType const y) const;

    bool inBounds(IndexType const x, IndexType const y) const;

    const IndexType y_size;
    const IndexType x_size;
    const MatrixConfig config{false};

private:
    std::vector<Type> data;
};

template<typename Type, typename IndexType>
std::ostream &operator<<(std::ostream &os, Matrix<Type, IndexType> const &matrix) {
    os << "Matrix<\n";
    for (int row = 0; row < matrix.y_size; ++row) {
        for (int col = 0; col < matrix.x_size; ++col) {
            os << matrix.at(col, row);
            if (col < matrix.x_size - 1) {
                os << ",";
            }
            if (col == matrix.x_size - 1 && matrix.config.repeatX) {
                os << " -> inf";
            }
        }
        os << "\n";
    }
    os << ">\n";
    return os;
}


void test_matrix();