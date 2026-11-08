package dev.saberlabs.springcoffee.prototype;

/**
 * Something that can hand back an independent copy of itself. Used for the "order it again"
 * flow: cloning a past order's coffee/extras/price into a fresh pending order, without a real
 * constructor call that would need every field re-supplied by the caller.
 */
public interface Prototype<T> {
    T copy();
}
