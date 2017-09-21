package io.jeti.linalg.matrix.utils;

import io.jeti.linalg.matrix.Mat;
import io.jeti.linalg.matrix.Vec;
import java.util.Collection;
import java.util.List;

public class Check {

    /**
     * Check that all of the sub collections in the provided collection are the same size
     * This is done by first getting the size of the first collection, and then
     * comparing that to the rest. There is no null checking here, so if a null is encountered at
     * any point, an exception will be thrown.
     */
    public static <T extends Collection> void sameSize(Collection<T> A) {
        int s = -1;
        for (Collection<?> c : A) {
            if (s < 0)
                s = c.size();
            else if (s != c.size())
                throw new IllegalArgumentException("Sub collections are not all the same size.");
        }
    }

    /**
     * Check that all of the sub arrays in the provided arrays are the same size
     * This is done by first getting the size of the first subarray, and then
     * comparing that to the rest. There is no null checking here, so if a null is encountered at
     * any point, an exception will be thrown.
     */
    public static <T> void sameSize(T[][] A) {
        int s = -1;
        for (T[] c : A) {
            if (s < 0)
                s = c.length;
            else if (s != c.length)
                throw new IllegalArgumentException("Sub arrays are not all the same size.");
        }
    }

    /**
     * Check that the input is not zero.
     */
    public static void notZero(int i) {
        if (i == 0)
            throw new IllegalArgumentException("The input cannot be 0.");
    }

    /**
     * Check that the input is zero.
     */
    public static void zero(int i) {
        if (i != 0)
            throw new IllegalArgumentException("The input must be 0.");
    }

    /**
     * Check that the inputs are equal.
     */
    public static void equal(int i, int j) {
        if (i != j)
            throw new IllegalArgumentException("Arguments must be equal.");
    }


    /**
     * Check that the input is positive.
     */
    public static void positive(int i) {
        if (i <= 0)
            throw new IllegalArgumentException("The input must be positive.");
    }

    /**
     * Check that index is in [lower,upper).
     */
    public static void inBounds(int index, int lower, int upper) {
        if (index < lower)
            throw new IllegalArgumentException(index + " cannot be less than " + lower);
        if (index >= upper)
            throw new IllegalArgumentException(
                    index + " cannot be greater than or equal to " + upper);
    }

    /**
     * Check that the {@link Vec}s have the same number of elements.
     */
    public static void sameSize(Vec<?> A, Vec<?> B) {
        if (A.size() != B.size())
            throw new IllegalArgumentException(
                    "Vectors must have the same size. Instead, they are " + A.size()
                            + " and " + B.size() + ", respectively.");
    }

    /**
     * Check that the matrices have the same number of rows.
     */
    public static <S extends Mat, T extends Mat> void sameNumberOfRows(S A, T B) {
        if (A.rows() != B.rows())
            throw new IllegalArgumentException(
                    "Matrices should have the same number of rows. Instead, one matrix has "
                            + A.rows() + " rows, and the other matrix has " + B.rows() + " rows.");
    }

    /**
     * Check that the matrices have the same number of columns.
     */
    public static <S extends Mat, T extends Mat> void sameNumberOfCols(S A, T B) {
        if (A.cols() != B.cols())
            throw new IllegalArgumentException(
                    "Matrices should have the same number of columns. Instead, one matrix has "
                            + A.cols() + " columns, and the other matrix has " + B.cols()
                            + " columns.");
    }

    /**
     * Check that the matrices have the same dimensions.
     */
    public static <S extends Mat<S>, T extends Mat<T>> void sameDimensions(S A, T B) {
        Check.sameNumberOfRows(A, B);
        Check.sameNumberOfCols(A, B);
    }

    /**
     * Check that the matrix has the specified number of rows and columns.
     */
    public static <Matrix extends Mat<Matrix>> void dimensions(Matrix A, int rows, int cols) {
        if (A.rows() != rows || A.cols() != cols)
            throw new IllegalArgumentException("The matrix must be " + rows + " x " + cols
                    + ". Instead, it is is " + A.cols() + " x " + A.rows());
    }

    /**
     * Check that each of the matrices in the Collection has the same
     * dimensions.
     */
    public static <Matrix extends Mat<Matrix>> void sameDimensions(Collection<Matrix> A) {
        int rows = -1;
        int cols = -1;
        for (Matrix mat : A) {
            if (rows == -1) {
                rows = mat.rows();
                cols = mat.cols();
            } else {
                Check.dimensions(mat, rows, cols);
            }
        }
    }

    /**
     * Ensure that a is divisible by b
     */
    public static void divisibleBy(int a, int b) {
        if (a % b != 0)
            throw new IllegalArgumentException(a + " is not divisible by " + b);
    }

    /**
     * Ensure that A is multipliable by B.
     */
    public static <Matrix extends Mat<Matrix>> void multipliable(Matrix A, Matrix B) {
        if (A.cols() != B.rows())
            throw new IllegalArgumentException(
                    "The provided matrices cannot be multiplied. The first matrix has " + A.cols()
                            + " columns, and the second matrix has " + B.rows() + " rows.");
    }

    public static <Matrix extends Mat<Matrix>> void isSquare(Matrix A) {
        if (A.rows() != A.cols())
            throw new IllegalArgumentException(
                    "The input should be square. Instead, it is " + A.cols() + " x " + A.rows());
    }

    public static <Matrix extends Mat<Matrix>> void inBounds(Matrix A, int row, int col) {
        if (row > A.rows() || col > A.cols())
            throw new IllegalArgumentException(
                    "Attempting to access a point outside of this matrix.");
    }

    public static void nonNegativeIndex(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("The index is negative.");
        }
    }

    public static <Matrix extends Mat<Matrix>> void sameNumberOfCoefficients(Collection<Matrix> A,
                                                                             Collection<Matrix> B) {
        if (A.size() != B.size()) {
            throw new IllegalArgumentException(
                    "B should have the same number of coefficients as A.");
        }
    }

    public static <Matrix extends Mat<Matrix>> void sameNumberOfCoefficientsOrOneLess(
            List<Matrix> A, List<Matrix> B) {
        if (A.size() > B.size()) {
            throw new IllegalArgumentException(
                    "B should have the same number of coefficients as A.");
        } else if (A.size() < B.size() - 1) {
            throw new IllegalArgumentException(
                    "A should have either the same number of coefficients as B, or one less (in which case, we assume A is monic).");
        } else if (B.size() == 0) {
            throw new IllegalArgumentException("A and B cannot be empty.");
        }
    }

    public static <Matrix extends Mat<Matrix>> void enoughCoefficients(List<Matrix> A, int n,
                                                                       int last) {
        if (last < n || last >= A.size()) {
            throw new IllegalArgumentException("Not enough matrices " + A.size()
                    + " to construct matrix of size " + (n + 1) + "x" + (last - n + 1));
        }
    }

}
