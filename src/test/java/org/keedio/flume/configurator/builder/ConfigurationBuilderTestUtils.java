package org.keedio.flume.configurator.builder;

import java.util.Map;

import org.keedio.flume.configurator.constants.FlumeConfiguratorConstants;
import org.keedio.flume.configurator.structures.LinkedProperties;

class ConfigurationBuilderTestUtils {

    static int calculatePropertiesTotalNumber (Map<String,LinkedProperties> configurationInitialMap) {
    	
    	int result = 0;
    	
    	if (configurationInitialMap != null) {
    		for (String agentName : configurationInitialMap.keySet()) {
    			LinkedProperties agentProperties = configurationInitialMap.get(agentName);
    			result = result + agentProperties.size();
    		}
    	}
    	
    	return result;
    }
    
    
    static boolean containsAgentElements(Map<String,LinkedProperties> configurationInitialMap, String prefixElement) {
    	boolean containsAgentElements = false;
    	
    	if (configurationInitialMap != null) {
    		for (String agentName : configurationInitialMap.keySet()) {
    			LinkedProperties agentProperties = configurationInitialMap.get(agentName);
    			for (Object agentProperty : agentProperties.entrySet()) {
    				Map.Entry<?,?> agentPropertyEntry = (Map.Entry<?,?>) agentProperty;
    				
    				String propertyKey = (String) agentPropertyEntry.getKey();

    				String[] propertyKeyPartsArray = propertyKey.split("\\" + FlumeConfiguratorConstants.DOT_SEPARATOR);
    				
    				if ((propertyKeyPartsArray.length == 2) && (propertyKeyPartsArray[1].equals(prefixElement))) {
    					containsAgentElements = true;
    				}
    				
    			}
    		}
    	} 
    	
    	return containsAgentElements;
    }
    
    
    static boolean containsSourcesInterceptors(Map<String,LinkedProperties> configurationInitialMap) {
    	boolean containsSourcesInterceptors = false; 
    	
    	if (configurationInitialMap != null) {
    		for (String agentName : configurationInitialMap.keySet()) {
    			LinkedProperties agentProperties = configurationInitialMap.get(agentName);
    			for (Object agentProperty : agentProperties.entrySet()) {
    				Map.Entry<?,?> agentPropertyEntry = (Map.Entry<?,?>) agentProperty;
    				
    				String propertyKey = (String) agentPropertyEntry.getKey();

    				String[] propertyKeyPartsArray = propertyKey.split(FlumeConfiguratorConstants.DOT_REGEX);
    				
    				if ((propertyKeyPartsArray.length == 4) && (propertyKeyPartsArray[3].equals(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY))) {
    					containsSourcesInterceptors = true;
    				}
    				
    			}
    		}
    	}     	
    	
    	return containsSourcesInterceptors;
    }
    
    
    static boolean containsInterceptorsProperties(Map<String,LinkedProperties> configurationInitialMap) {
    	boolean containsInterceptorsProperties = false; 
    	
    	if (configurationInitialMap != null) {
    		for (String agentName : configurationInitialMap.keySet()) {
    			LinkedProperties agentProperties = configurationInitialMap.get(agentName);
    			for (Object agentProperty : agentProperties.entrySet()) {
    				Map.Entry<?,?> agentPropertyEntry = (Map.Entry<?,?>) agentProperty;
    				
    				String propertyKey = (String) agentPropertyEntry.getKey();

    				String[] propertyKeyPartsArray = propertyKey.split(FlumeConfiguratorConstants.DOT_REGEX);
    				
    				if ((propertyKeyPartsArray.length > 4) && (propertyKeyPartsArray[3].equals(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY))) {
    					containsInterceptorsProperties = true;
    				}
    				
    			}
    		}
    	}     	
    	
    	return containsInterceptorsProperties;
    }    
    
    
    
    
    static boolean containsElementsProperties(Map<String,LinkedProperties> configurationInitialMap, String prefixElement) {
    	boolean containsElementsProperties = false; 
    	
    	if (configurationInitialMap != null) {
    		for (String agentName : configurationInitialMap.keySet()) {
    			LinkedProperties agentProperties = configurationInitialMap.get(agentName);
    			for (Object agentProperty : agentProperties.entrySet()) {
    				Map.Entry<?,?> agentPropertyEntry = (Map.Entry<?,?>) agentProperty;
    				
    				String propertyKey = (String) agentPropertyEntry.getKey();

    				String[] propertyKeyPartsArray = propertyKey.split(FlumeConfiguratorConstants.DOT_REGEX);
    				
    				if ((propertyKeyPartsArray.length > 2) && (propertyKeyPartsArray[1].equals(prefixElement))) {
    					
    					if (FlumeConfiguratorConstants.SOURCES_PROPERTY.equals(prefixElement)) {
    						//Source property but not interceptor property
    						if (!propertyKeyPartsArray[3].equals(FlumeConfiguratorConstants.INTERCEPTORS_PROPERTY)) {
    							containsElementsProperties = true;
    						}
    						
    					} else {
    						containsElementsProperties = true;
    					}
    					
    				}
    				
    			}
    		}
    	}     	
    	
    	return containsElementsProperties;
    }        
}
