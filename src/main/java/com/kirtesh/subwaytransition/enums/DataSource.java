package com.kirtesh.subwaytransition.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * We could have multiple DataSource. It would be used to let DataSourceFactory know from which data source we want to fetch data.
 */
public enum DataSource {
    CSV_FILE("CSV_FILE");

    private static List<String> dataSourceList = new ArrayList<>();
    private String _dataSource;

    private DataSource(String _dataSource) {
        this._dataSource = _dataSource;
    }

    public String getDataSource() {
        return _dataSource;
    }

    static {
        for(DataSource dataSource : DataSource.values()) {
            dataSourceList.add(dataSource._dataSource);
        }
    }

    public static List<String> getPropertiesList() {
        List<String> dataSources = new ArrayList<String>();
        dataSources.addAll(dataSourceList);
        return dataSources;
    }

    public static boolean isValidDataSource(String dataSource) {
        dataSource = dataSource.toUpperCase();

        if(dataSourceList.contains(dataSource)) {
            return true;
        }
        return false;
    }
}