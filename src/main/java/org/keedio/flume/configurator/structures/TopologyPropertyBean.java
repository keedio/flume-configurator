package org.keedio.flume.configurator.structures;

public class TopologyPropertyBean {

    private String propertyComment;
    private String appliedElement;
    private String propertyValue;


    public TopologyPropertyBean(String propertyComment, String appliedElement, String propertyValue) {
        this.propertyComment = propertyComment;
        this.appliedElement = appliedElement;
        this.propertyValue = propertyValue;
    }

    public String getPropertyComment() {
        return propertyComment;
    }

    public void setPropertyComment(String propertyComment) {
        this.propertyComment = propertyComment;
    }

    public String getAppliedElement() {
        return appliedElement;
    }

    public void setAppliedElement(String appliedElement) {
        this.appliedElement = appliedElement;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertiesValue) {
        this.propertyValue = propertiesValue;
    }
}
