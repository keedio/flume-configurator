package org.keedio.flume.configurator.exceptions;

public class FlumeConfiguratorException extends RuntimeException{

    public FlumeConfiguratorException() {
        super();
    }

    public FlumeConfiguratorException(String s, Throwable t) {
        super(s, t);
    }

    public FlumeConfiguratorException(String s) {
        super(s);
    }

    public FlumeConfiguratorException(Throwable t) {
        super(t);
    }
}
