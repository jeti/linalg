package io.jeti.linalg.matrix;

import io.jeti.linalg.matrix.interfaces.Tsr;

/**
 * The sole purpose of these interfaces is to enable all of the concrete classes
 * to have either a concrete implementation of {@link Tsr} OR some List as the
 * field which holds the data. This is the simplest way of allowing the
 * {@link Tsr} implementations to permit views. Specifically, suppose we have a
 * {@link io.jeti.linalg.matrix.Vector} with the data {0,1,2,3,4} and we want a
 * view of the subvector {1,2,3}. So that we really return a view of the data
 * (not just a copy), we need to pass the original list {0,1,2,3,4} to the new
 * vector and tell it to "get" its elements from that original list. Now the
 * problem comes in when we try to get a view of the new vector {1,2,3}. For
 * instance, let's suppose we want to pull off the first two elements. Either we
 * transform the indices and stride to handle this, or we create a wrapper like
 * this one. Transforming the indices is preferable from a speed point of view,
 * but more difficult to compute. So we chalk that up to "future work".
 */
interface Gettable {

    interface Sizeable {
        int size();
    }

    interface Gettable1<T> extends Sizeable {

        T get(int a);
    }

    interface Gettable2<T> extends Sizeable {

        T get(int a, int b);
    }

    interface Gettable3<T> extends Sizeable {

        T get(int a, int b, int c);
    }

    interface Gettable4<T> extends Sizeable {

        T get(int a, int b, int c, int d);
    }

    interface Gettable5<T> extends Sizeable {

        T get(int a, int b, int c, int d, int e);
    }

    interface Gettable6<T> extends Sizeable {

        T get(int a, int b, int c, int d, int e, int f);
    }

    class List<T> implements Gettable1<T> {

        private final java.util.List<T> list;

        public List(java.util.List<T> list) {
            this.list = list;
        }

        @Override
        public T get(int a) {
            return list.get(a);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    class List2<T> implements Gettable2<T> {

        private final java.util.List<java.util.List<T>> list;

        public List2(java.util.List<java.util.List<T>> list) {
            this.list = list;
        }

        @Override
        public T get(int a, int b) {
            return list.get(a).get(b);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    class MutableList<T> implements Gettable1<T> {

        private final java.util.List<T> list;

        public MutableList(java.util.List<T> list) {
            this.list = list;
        }

        @Override
        public T get(int a) {
            return list.get(a);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    class MutableList2<T> implements Gettable2<T> {

        private final java.util.List<java.util.List<T>> list;

        public MutableList2(java.util.List<java.util.List<T>> list) {
            this.list = list;
        }

        @Override
        public T get(int a, int b) {
            return list.get(a).get(b);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

}
