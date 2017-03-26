package com.task.utils;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

/**
 * Created by blanke on 2017/3/26.
 */
public class DataTableUtils {
    public static DataTablesOutput convert(DataTablesOutput source) {
        DataTablesOutput result = new DataTablesOutput();
        result.setDraw(source.getDraw());
        result.setError(source.getError());
        result.setRecordsFiltered(source.getRecordsFiltered());
        result.setRecordsTotal(source.getRecordsTotal());
        return result;
    }
}
