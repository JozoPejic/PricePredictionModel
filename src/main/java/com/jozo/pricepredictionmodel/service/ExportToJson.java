package com.jozo.pricepredictionmodel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jozo.pricepredictionmodel.model.ModelInputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service

public class ExportToJson {

    @Autowired
    CombineTrainingData combineTrainingData;


    public void exportToJson(String symbol) throws IOException {
        List<ModelInputDTO> data = combineTrainingData.fetchAndCombineData(symbol);
        ObjectMapper objectMapper = new ObjectMapper();
        String file_path = symbol.toUpperCase() + "_data.json";
        objectMapper.writeValue(new File(file_path), data);
        System.out.println("Data saved");
    }
}
