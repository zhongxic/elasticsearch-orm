package org.example.elasticsearch.orm.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsPageStarter {

    private static final ThreadLocal<EsPage<?>> CONTEXT = new ThreadLocal<>();

    public static void start(long current, long size) {
        EsPage<?> esPage = new EsPage<>();
        esPage.setCurrent(current);
        esPage.setSize(size);
        CONTEXT.set(esPage);
    }

    @SuppressWarnings("unchecked")
    public static <E> EsPage<E> get() {
        return (EsPage<E>) CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }

}
