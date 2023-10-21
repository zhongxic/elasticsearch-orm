package org.example.elasticsearch.orm.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EsPage<T> {

    private long current;

    private long size;

    private long total;

    private List<T> records;

    @SuppressWarnings("unused")
    public EsPage(long current, long size, long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
    }

    @SuppressWarnings("unused")
    public long getPages() {
        if (this.size == 0) {
            return 0;
        }
        long pages = this.total / this.size;
        if (this.total % this.size > 0) {
            pages++;
        }
        return pages;
    }

}
