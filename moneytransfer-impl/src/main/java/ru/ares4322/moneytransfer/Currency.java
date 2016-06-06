package ru.ares4322.moneytransfer;

import java.util.HashMap;
import java.util.Map;

public enum Currency {
    RUB(1),
    USD(2),
    GBP(3);

    public final int id;

    Currency(int id) {
        this.id = id;
    }

    private static final Map<Integer, Currency> mapping = new HashMap<>();

    static {
        for (Currency value : Currency.values()) {
            mapping.put(value.id, value);
        }
    }

    public static Currency byId(Integer id) {
        return Currency.mapping.get(id);
    }
}
