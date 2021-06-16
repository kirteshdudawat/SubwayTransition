package com.kirtesh.subwaytransition.utils.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.List;

@Component
public class CSVReaderUtility {
    /**
     * Utility to read a CSV file form classpath resource folder.
     *
     * @param fileName -> filename to be read.
     * @param type -> Entity class type.
     * @param <T> Entity class for a CSV
     * @return List of Type T
     * @throws Exception for cases such as no file exist, invalid entity used to fetch data etc.
     */
    public static <T> List readCsvFile(String fileName, Class<T> type) throws Exception {
        try(InputStream inputStream = CSVReaderUtility.class.getResourceAsStream(String.format("/%s",fileName))) {
            try(InputStreamReader isr = new InputStreamReader(inputStream)) {
                try(BufferedReader reader = new BufferedReader(isr)) {
                    CsvToBean<T> csvToBean = new CsvToBeanBuilder(reader)
                            .withType(type)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();

                    return csvToBean.parse();
                }
            }
        }
    }
}