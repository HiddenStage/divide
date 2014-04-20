package io.divide.client.cache.test;

import java.util.ArrayList;
import java.util.List;

public class Person {

    private String firstname;
    private String lastname;

    private List<Address> addresses = new ArrayList<Address>();

    private Address defaultAddress;

    private PrivateData privateData;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Address defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public PrivateData getPrivateData() {
        return privateData;
    }

    public void setPrivateData(PrivateData privateData) {
        this.privateData = privateData;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Person[firstname=").append(firstname).append(",");
        sb.append("lastname=").append(lastname).append(",");
        sb.append("defaultAddress=").append(defaultAddress).append(",");
        sb.append("addresses=").append(addresses).append(",");
        sb.append("privateData=").append(privateData).append("]");
        return sb.toString();
    }
}
