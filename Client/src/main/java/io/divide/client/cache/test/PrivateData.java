package io.divide.client.cache.test;

public class PrivateData {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrivateData[username=").append(username).append(",");
        sb.append("password=").append(password).append("]");
        return sb.toString();
    }

}
