package org.keedio.flume.configurator.structures;

import java.util.ArrayList;
import java.util.List;

public class FlumeLinesProperties {

    private LinkedProperties properties;
    private List<String> lines;

    public FlumeLinesProperties() {
        properties = new LinkedProperties();
        lines = new ArrayList<>();
    }


    public LinkedProperties getProperties() {
        return properties;
    }

    public void setProperties(LinkedProperties properties) {
        this.properties = properties;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
