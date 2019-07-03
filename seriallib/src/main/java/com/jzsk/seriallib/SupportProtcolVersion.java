package com.jzsk.seriallib;

/**
 * 暂分开支持，
 * \\todo 混合支持
 */
public enum  SupportProtcolVersion{
    V21("V2.1",21), V40("V4.0",40);
    private String name;
    private int version;
    private SupportProtcolVersion(String name, int version) {
        this.name = name;
        this.version = version;
    }
    public static String getName(int version) {
        for (SupportProtcolVersion c : SupportProtcolVersion.values()) {
            if (c.getVersion() == version) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
