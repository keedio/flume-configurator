package org.keedio.flume.configurator.builder;

import org.keedio.flume.configurator.structures.FlumeTopology;

import java.util.List;
import java.util.Map;

public class FlumeTopologyReversePropertiesGeneratorTestUtils {

    private FlumeTopologyReversePropertiesGeneratorTestUtils() {
        super();
    }


    static int getFlumeElementTypeElementsNumber(List<FlumeTopology> flumeTopologyList, String flumeTopologyType) {

        int flumeElementTypeElementsNumber = 0;

        if (flumeTopologyList != null && flumeTopologyType != null && !flumeTopologyType.isEmpty()) {

            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                String flumeTopologyElementType = flumeTopologyElement.getType();
                if (flumeTopologyType.equals(flumeTopologyElementType)) {
                    flumeElementTypeElementsNumber++;
                }
            }
        }

        return flumeElementTypeElementsNumber;
    }



    static int getFlumeElementTypeElementPropertiesNumber(List<FlumeTopology> flumeTopologyList) {

        int flumeElementTypeElementPropertiesNumber = 0;

        if (flumeTopologyList != null) {

            for (FlumeTopology flumeTopologyElement : flumeTopologyList) {
                Map<String, String> flumeTopologyElementData = flumeTopologyElement.getData();
                if (flumeTopologyElementData != null) {
                    for(String propertyName : flumeTopologyElementData.keySet()) {
                        flumeElementTypeElementPropertiesNumber++;
                    }
                }
            }
        }

        return flumeElementTypeElementPropertiesNumber;
    }
}
