package com.fpa.mainsupport.core;

/**
 *
 */
public interface Callback<T extends Object> {
    void call(T... values);
}
