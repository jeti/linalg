package io.jeti.linalg.matrix;

import io.jeti.linalg.matrix.Gettable.Gettable1;
import io.jeti.linalg.matrix.Gettable.Gettable2;
import io.jeti.linalg.matrix.Gettable.Gettable3;
import io.jeti.linalg.matrix.Gettable.Gettable4;
import io.jeti.linalg.matrix.Gettable.Gettable5;
import io.jeti.linalg.matrix.Gettable.Gettable6;

/**
 * This is essentially {@link Gettable} with setters.
 */
interface Settable {

    interface Settable1<T> extends Gettable1<T> {

        T set(int a, T val);
    }

    interface Settable2<T> extends Gettable2<T> {

        T set(int a, int b, T val);
    }

    interface Settable3<T> extends Gettable3<T> {

        T set(int a, int b, int c, T val);
    }

    interface Settable4<T> extends Gettable4<T> {

        T set(int a, int b, int c, int d, T val);
    }

    interface Settable5<T> extends Gettable5<T> {

        T set(int a, int b, int c, int d, int e, T val);
    }

    interface Settable6<T> extends Gettable6<T> {

        T set(int a, int b, int c, int d, int e, int f, T val);
    }

    class List<T> implements Settable1<T> {

        private final java.util.List<T> list;

        public List(java.util.List<T> list) {
            this.list = list;
        }

        @Override
        public T get(int a) {
            return list.get(a);
        }

        @Override
        public T set(int a, T val) {
            return list.set(a, val);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    class List2<T> implements Settable2<T> {

        private final java.util.List<java.util.List<T>> list;

        public List2(java.util.List<java.util.List<T>> list) {
            this.list = list;
        }

        @Override
        public T get(int a, int b) {
            return list.get(a).get(b);
        }

        @Override
        public T set(int a, int b, T val) {
            return list.get(a).set(b, val);
        }

        @Override
        public int size() {
            return list.size();
        }
    }
}
