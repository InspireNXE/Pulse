package org.inspirenxe.server;

public class Configuration {
    private String name;
    private int port;

    private Configuration() {}

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "name='" + name + '\'' +
                ", port=" + port +
                '}';
    }
}
