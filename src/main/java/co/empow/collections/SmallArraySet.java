package co.empow.collections;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * Created by assaf on 21/06/2016.
 */

public class SmallArraySet<E> implements Set<E> {

    private ArrayList<E> set;

    public SmallArraySet() {
        set = new ArrayList<>();
    }

    public SmallArraySet(int initialCapacity) {
        set = new ArrayList<>(initialCapacity);
    }

    public SmallArraySet(Collection<? extends E> c) {
        this(c.size());
        addAll(c);
    }

    public static <E> SmallArraySet<E> newSet(E... elements) {
        SmallArraySet set = new SmallArraySet(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean add(E e) {
        if (set.contains(e)) {
            return false;
        }

        return set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;

        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Set)) {
            return false;
        }
        Set thatSet = obj instanceof SmallArraySet ? Sets.newHashSet(((SmallArraySet) obj).set) : (Set) obj;
        return Sets.newHashSet(this.set).equals(thatSet);
    }


    @Override
    public int hashCode() {
        return Sets.newHashSet(this.set).hashCode();
    }

    @Override
    public String toString() {
        return "SmallArraySet{" +
                "set=" + set +
                '}';
    }
}