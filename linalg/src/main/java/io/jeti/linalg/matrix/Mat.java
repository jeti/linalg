package io.jeti.linalg.matrix;

import io.jeti.linalg.matrix.utils.Check;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A {@link Mat} is a two-dimensional data structure which supports element-wise
 * addition, subtraction, and multiplication, as well as special operations,
 * such as matrix multiplication. However, so that this interface can support
 * immutable implementations, setters and other mutable operations are not
 * implemented.
 * </p>
 * <p>
 * Note that {@link Mat} implementations should use the signature:
 * </p>
 * 
 * <pre>
 * public class Matrix implements Mat&lt;Matrix&gt;
 * </pre>
 * <p>
 * because the generic type supplied to {@link Mat} is used as the input and
 * return type for many methods in the {@link Mat} interface. For instance,
 * consider the addition operation:
 * </p>
 * 
 * <pre>
 * public T plus(T B)
 * </pre>
 * <p>
 * this implies that an instance of an implementation cannot be easily added to
 * an instance of a different implementation. While this may at first seem
 * illogical, it was done on purpose so that implementations can use whatever
 * optimizations are possible to make them as fast as possible. For example, if
 * we have an implementation that creates matrices on the GPU, then for the sake
 * of speed, we should only require that it supports addition and other
 * operations for other matrices that live on the GPU.
 * </p>
 * Since this interface is very similar in spirit to the {@link Vec} interface,
 * we suggest that you view the documentation there for furthermore pointers on
 * using this interface, particularly the constructors.
 */
public interface Mat<T extends Mat<T>> extends Tsr<T> {

    interface Filler {
        double apply(int row, int col);
    }

    /*
     * --------------------------------------------------
     *
     * Constructors
     *
     * --------------------------------------------------
     */

    /**
     * @return A (rows x cols) {@link Mat}, where all of the elements are set
     *         using the specified {@link Filler}.
     */
    T newInstance(int rows, int cols, Filler filler);

    /**
     * @return A (rows x 1) {@link Mat}, where all values are set to zero.
     */
    default T newInstance(int rows) {
        return newInstance(rows, 1, (row, col) -> 0d);
    }

    /**
     * @return A (rows x cols) {@link Mat}, where all values are set to zero.
     */
    default T newInstance(int rows, int cols) {
        return newInstance(rows, cols, (row, col) -> 0d);
    }

    /**
     * @return A (rows x cols) {@link Mat}, where all values are set to val.
     */
    default T newInstance(int rows, int cols, Number val) {
        return newInstance(rows, cols, (row, col) -> val.doubleValue());
    }

    /**
     * @return A {@link Mat} wrapper around the provided data, where the Matrix
     *         will have data.size() rows, and data.get(0).size() columns. Note
     *         that it is up to the implementation to decide whether this is a
     *         copy or reference to the data. However, all implementations
     *         should check that none of the rows are null, and that all of the
     *         rows have the same length.
     */
    default T newInstance(List<List<Number>> data) {
        Check.sameSize(data);
        return newInstance(data.size(), data.get(0).size(),
                (row, col) -> data.get(row).get(col).doubleValue());
    }

    /**
     * @return A {@link Mat} using the provided data, where the Matrix will have
     *         data.length rows, and data[0].length columns. Note that it is up
     *         to the implementation to decide whether this is a copy or
     *         reference to the data. However, all implementations should check
     *         that none of the rows is null, and that all of the rows have the
     *         same length.
     */
    default T newInstance(Number[][] data) {
        Check.sameSize(data);
        return newInstance(data.length, data[0].length, (row, col) -> data[row][col].doubleValue());
    }

    /**
     * @return A (rows x cols) Matrix, where all values are set to 1.
     */
    default T ones(int rows, int cols) {
        return newInstance(rows, cols, (row, col) -> 1d);
    }

    /**
     * @return A (rows x cols) Matrix, where all values are set to 0.
     */
    default T zeros(int rows, int cols) {
        return newInstance(rows, cols, (row, col) -> 0d);
    }

    /**
     * @return A (rows x rows) identity Matrix.
     */
    default T I(int rows) {
        return newInstance(rows, rows, (row, col) -> row == col ? 1d : 0d);
    }

    /**
     * @return A (rows x cols) Matrix of uniform random number in [0,1].
     */
    default T rand(int rows, int cols) {
        return newInstance(rows, cols, (row, col) -> random.nextDouble());
    }

    /**
     * @return A (rows x cols) Matrix of Gaussian random number drawn from a
     *         distribution with mean 0 and variance 1.
     */
    default T randn(int rows, int cols) {
        return newInstance(rows, cols, (row, col) -> random.nextGaussian());
    }

    /*
     * --------------------------------------------------
     *
     * Getters
     *
     * --------------------------------------------------
     */

    /**
     * @return a boolean indicating whether rows()=cols()=1.
     */
    default boolean isScalar() {
        return (rows() == 1) && (cols() == 1);
    }

    /**
     * @return A deep copy of the data contained in this {@link Mat}.
     *         Specifically, if <code>A = this.deepDataCopy()</code>, then
     *         <code>A.get(i)</code> denotes the i^th row of this matrix.
     */
    default List<List<Double>> toList() {
        List<List<Double>> out = new ArrayList<>(rows());
        for (int r = 0; r < rows(); r++) {
            List<Double> row = new ArrayList<>(cols());
            for (int c = 0; c < cols(); c++) {
                row.add(get(r, c));
            }
            out.add(row);
        }
        return out;
    }

    /**
     * @return The number of rows of this {@link Mat}.
     */
    int rows();

    /**
     * @return The number of columns of this {@link Mat}.
     */
    int cols();

    /**
     * @return A view of the specified row of this {@link Mat}.
     */
    default T row(int r) {
        return get(r, r + 1, 0, cols());
    }

    /**
     * @return A view of the specified column of this {@link Mat}.
     */
    default T col(int c) {
        return get(0, rows(), c, c + 1);
    }

    /**
     * @return The (row,col) element of this {@link Mat}, where row and col
     *         indices start from 0.
     */
    Double get(int row, int col);

    /**
     * @return {@link #get(int, int, int, int, int, int)}, where
     * 
     *         <pre>
     *         rowStride = fromRow &lt; toRow ? 1 : -1;
     *         colStride = fromCol &lt; toCol ? 1 : -1;
     *         </pre>
     */
    default T get(int fromRow, int toRow, int fromCol, int toCol) {
        int rowStride = fromRow < toRow ? 1 : -1;
        int colStride = fromCol < toCol ? 1 : -1;
        return get(fromRow, toRow, rowStride, fromCol, toCol, colStride);
    }

    /**
     * TODO: Documentation. This was copied from the {@link Vec} interface since
     * the implementation is similar, but should be updated nonetheless.
     *
     * @return A view of the submatrix in [fromIndex,toIndex), that is,
     *         inclusive "fromIndex" and exclusive "toIndex", with the specified
     *         stride. For example, if this {@link Vec} contains the data
     *         [0,1,2,3,4,5,6,7,8], then get(1,6,2) means the subvector:
     *         <ul>
     *         <li>starting (and including) the value at index 1</li>
     *         <li>up to (but excluding) the value at index 6</li>
     *         <li>with a step of 2</li>
     *         </ul>
     *         <p>
     *         that is, the subvector [1,3,5]. All implementations must impose
     *         the constraint that toIndex!=fromIndex, because otherwise the
     *         returned vector would be null, which is not allowed.
     *         </p>
     *         <p>
     *         Finally, note that stride cannot be zero, and that stride must
     *         have the same sign as the difference (toIndex-fromIndex). For
     *         example, if stride=-2, fromIndex=7, and toIndex=1, then the
     *         previous example yields a vector with the data [7,5,3].
     *         </p>
     */
    T get(int fromRow, int toRow, int rowStride, int fromCol, int toCol, int colStride);

    /*
     * --------------------------------------------------
     *
     * Matrix/Self Operations
     *
     * --------------------------------------------------
     */

    /**
     * @return this^i = this * ... * this, where i is a nonnegative integer.
     */
    @Override
    default T pow(int pw) {
        /* TODO Obviously a lot of room for improvement here. */
        Check.nonNegativeIndex(pw);
        T C = I(rows());
        for (int i = 0; i < pw; i++)
            C = this.times(C);
        return C;
    }

    /**
     * @return this^i (element-wise)
     */
    default T powElementwise(int i) {
        return Tsr.super.pow(i);
    }

    /**
     * @return this^T
     */
    default T transpose() {
        return newInstance(cols(), rows(), (row, col) -> get(col, row));
    }

    /**
     * @return the vectorized matrix. For instance, if this is an nxm matrix
     *         where this = [c1,...,cm] and ci denotes the i^th column of this,
     *         then this.vec() is a nmx1 matrix, where
     *         <p>
     * 
     *         <pre>
     *         this.vec() = [ c1 ]
     *                      [ .. ]
     *                      [ cm ]
     *         </pre>
     */
    default T vec() {
        return newInstance(rows() * cols(), 1, (row, col) -> get(row % rows(), row / rows()));
    }

    /*
     * --------------------------------------------------
     *
     * Matrix/Matrix Operations
     *
     * --------------------------------------------------
     */

    /**
     * @return this * B (elementwise) using the rules laid out by the
     *         {@link #apply(Operation, Mat)} function.
     */
    default T timesElementwise(T B) {
        return Tsr.super.times(B);
    }

    /**
     * @return this * B, that is the matrix product. If either this or B are
     *         scalars, then this returns the element-wise product. Otherwise,
     *         this returns the matrix product.
     */
    @Override
    default T times(T B) {
        if (isScalar() || B.isScalar()) {
            return timesElementwise(B);
        } else {
            Check.equal(cols(), B.rows());
            return newInstance(rows(), B.cols(), (row, col) -> {
                double sum = 0.0;
                for (int i = 0; i < cols(); i++)
                    sum += get(row, i) * B.get(i, col);
                return sum;
            });
        }
    }

    @Override
    default T apply(Operation operation, T B) {
        if (isScalar()) {
            /* A = ( 1 x 1 ), B = (B.rows() x B.cols()) */
            return newInstance(B.rows(), B.cols(),
                    (row, col) -> operation.apply(get(0, 0), B.get(row, col)));
        } else if (B.isScalar()) {
            /* A = (rows x cols), B = (1 x 1) */
            return newInstance(rows(), cols(),
                    (row, col) -> operation.apply(get(row, col), B.get(0, 0)));
        } else {
            Check.sameNumberOfRows(this, B);
            Check.sameNumberOfCols(this, B);
            /* A = (rows x cols), B = (rows x cols) */
            return newInstance(rows(), cols(),
                    (row, col) -> operation.apply(get(row, col), B.get(row, col)));
        }
    }

    /*
     * --------------------------------------------------
     *
     * Matrix/Scalar Operations
     *
     * --------------------------------------------------
     */
    @Override
    default T apply(Operation operation, Number B) {
        return newInstance(rows(), cols(),
                (row, col) -> operation.apply(get(row, col), B.doubleValue()));
    }

    /*
     * --------------------------------------------------
     *
     * Print Functions
     *
     * --------------------------------------------------
     */

    /**
     * @return the String representation of the specified row of the Matrix.
     */
    default String rowToString(int row) {
        StringBuilder builder = new StringBuilder();
        for (int col = 0; col < cols(); col++)
            builder.append(format(get(row, col)));
        return builder.toString();
    }

    @Override
    default String asString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < rows(); row++)
            builder.append(rowToString(row)).append(System.getProperty("line.separator"));
        return builder.toString();
    }
}