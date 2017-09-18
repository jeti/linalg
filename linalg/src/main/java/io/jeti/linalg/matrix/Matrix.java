package io.jeti.linalg.matrix;

import io.jeti.linalg.matrix.Gettable.Gettable2;
import io.jeti.linalg.matrix.Gettable.List2;
import io.jeti.linalg.matrix.interfaces.Mat;
import java.util.ArrayList;
import java.util.List;

/**
 * A pure Java, immutable implementation of the {@link Mat} interface.
 */
public class Matrix implements Mat<Matrix>, Gettable2<Double> {

    /*
     * --------------------------------------------------
     *
     * Private Fields
     *
     * --------------------------------------------------
     */
    private final Gettable2<Double> data;

    private final int               rows;
    private final int               rowFrom;
    private final int               rowStride;

    private final int               cols;
    private final int               colFrom;
    private final int               colStride;

    /*
     * --------------------------------------------------
     *
     * Constructors
     *
     * --------------------------------------------------
     */
    @Override
    public Matrix newInstance(int rows, int cols, Filler filler) {
        return new Matrix(rows, cols, filler);
    }

    /**
     * See {@link #newInstance(int, int, Filler)}.
     */
    public Matrix(int rows, int cols, Filler filler) {
        Check.positive(rows);
        Check.positive(cols);
        List<List<Double>> tmp = new ArrayList<>(rows);
        for (int r = 0; r < rows; r++) {
            List<Double> row = new ArrayList<>(cols);
            for (int c = 0; c < cols; c++) {
                row.add(filler.apply(r, c));
            }
            tmp.add(row);
        }
        this.data = new List2<>(tmp);
        this.rows = rows;
        this.rowFrom = 0;
        this.rowStride = 1;
        this.cols = cols;
        this.colFrom = 0;
        this.colStride = 1;
    }

    /**
     * See {@link #newInstance(int)}.
     */
    public Matrix(int rows) {
        this(rows, 1, (row, col) -> 0d);
    }

    /**
     * See {@link #newInstance(int, int)}.
     */
    public Matrix(int rows, int cols) {
        this(rows, cols, (row, col) -> 0d);
    }

    /**
     * See {@link #newInstance(int, int, Number)}.
     */
    public Matrix(int rows, int cols, final Number val) {
        this(rows, cols, (row, col) -> val.doubleValue());
    }

    /**
     * See {@link #newInstance(List)}.
     */
    public Matrix(final List<List<Number>> data) {
        this(data.size(), data.get(0).size(), (row, col) -> data.get(row).get(col).doubleValue());
    }

    /**
     * See {@link #newInstance(Number[][])}.
     */
    public Matrix(final Number[][] data) {
        this(data.length, data[0].length, (row, col) -> data[row][col].doubleValue());
    }

    private Matrix(Matrix matrix, int fromRow, int toRow, int rowStride, int fromCol, int toCol,
            int colStride) {

        /* Make sure that selection is valid. */
        this.rows = checkSelection(fromRow, toRow, rowStride, matrix.rows());
        this.cols = checkSelection(fromCol, toCol, colStride, matrix.cols());

        /* Checks indicate that the inputs are valid. */
        this.data = matrix;
        this.rowFrom = fromRow;
        this.rowStride = rowStride;
        this.colFrom = fromCol;
        this.colStride = colStride;
    }

    /*
     * --------------------------------------------------
     *
     * Getters
     *
     * --------------------------------------------------
     */
    @Override
    public final int rows() {
        return rows;
    }

    @Override
    public final int cols() {
        return cols;
    }

    @Override
    public final Double get(int row, int col) {
        int r = index(row, rowFrom, rowStride, rows());
        int c = index(col, colFrom, colStride, cols());
        return data.get(r, c);
    }

    @Override
    public Matrix get(int fromRow, int toRow, int rowStride, int fromCol, int toCol,
            int colStride) {
        return new Matrix(this, fromRow, toRow, rowStride, fromCol, toCol, colStride);
    }

    /*
     * --------------------------------------------------
     *
     * Matrix/Matrix Operations
     *
     * --------------------------------------------------
     */
    @Override
    public final Matrix times(final Matrix B) {
        if (rows() == 1 && cols() == 1) {
            return B.times(get(0, 0));
        } else if (B.rows() == 1 && B.cols() == 1) {
            return times(B.get(0, 0));
        } else {
            Check.zero(cols() - B.rows());
            return newInstance(rows(), B.cols(), (row, col) -> {
                double sum = 0.0;
                for (int i = 0; i < cols(); i++) {
                    sum += get(row, i) * B.get(i, col);
                }
                return sum;
            });
        }
    }

    /*
     * --------------------------------------------------
     *
     * Other Functions
     *
     * --------------------------------------------------
     */
    @Override
    public String toString() {
        return asString();
    }

    @Override
    public int size() {
        return rows() * cols();
    }
}