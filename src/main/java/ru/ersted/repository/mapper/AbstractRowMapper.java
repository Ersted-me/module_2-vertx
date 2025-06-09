package ru.ersted.repository.mapper;


import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class AbstractRowMapper<T> implements RowMapper<T> {

    protected static <T> T safe(Supplier<T> getter) {
        try {
            return getter.get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
