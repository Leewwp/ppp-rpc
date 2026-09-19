package com.ppp.rpc.factory;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingletonFactoryTest {

    @Test
    void getInstanceShouldReturnSameInstanceForSameClass() {
        List instance1 = SingletonFactory.getInstance(ArrayList.class);
        List instance2 = SingletonFactory.getInstance(ArrayList.class);

        assertSame(instance1, instance2, "Same class should return same singleton instance");
    }

    @Test
    void getInstanceShouldReturnDifferentInstancesForDifferentClasses() {
        List listInstance = SingletonFactory.getInstance(ArrayList.class);
        StringBuilder sbInstance = SingletonFactory.getInstance(StringBuilder.class);

        assertNotSame(listInstance, sbInstance, "Different classes should return different instances");
    }

    @Test
    void getInstanceShouldThrowExceptionForNullClass() {
        assertThrows(IllegalArgumentException.class, () -> {
            SingletonFactory.getInstance(null);
        });
    }
}
