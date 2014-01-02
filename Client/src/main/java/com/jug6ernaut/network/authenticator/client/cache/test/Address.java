package com.jug6ernaut.network.authenticator.client.cache.test;

public class Address {

    private String street;
    private String zip;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Address[street=").append(street).append(",");
        sb.append("zip=").append(zip).append("]");
        return sb.toString();
    }

}
