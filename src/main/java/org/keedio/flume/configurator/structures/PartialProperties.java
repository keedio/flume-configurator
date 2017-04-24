package org.keedio.flume.configurator.structures;

public class PartialProperties {

    private LinkedProperties appliedElements;
    private LinkedProperties propertiesValues;
    private LinkedProperties propertiesComments;


    public PartialProperties() {
        appliedElements = new LinkedProperties();
        propertiesValues = new LinkedProperties();
        propertiesComments = new LinkedProperties();
    }


    public LinkedProperties getAppliedElements() {
        return appliedElements;
    }


    public void setAppliedElements(LinkedProperties appliedElements) {
        this.appliedElements = appliedElements;
    }


    public LinkedProperties getPropertiesValues() {
        return propertiesValues;
    }


    public void setPropertiesValues(LinkedProperties propertiesValues) {
        this.propertiesValues = propertiesValues;
    }


    public LinkedProperties getPropertiesComments() {
        return propertiesComments;
    }


    public void setPropertiesComments(LinkedProperties propertiesComments) {
        this.propertiesComments = propertiesComments;
    }




}

