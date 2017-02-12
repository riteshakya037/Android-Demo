
package com.calebtrevino.tallystacker.models.espn;


public class Type {

    public String id;
    public String shortDetail;
    public String detail;
    public String altDetail;
    public String description;
    public String name;
    public String state;
    public Boolean completed;

    @Override
    public String toString() {
        return "Type{" +
                "id='" + id + '\'' +
                ", shortDetail='" + shortDetail + '\'' +
                ", detail='" + detail + '\'' +
                ", altDetail='" + altDetail + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", completed=" + completed +
                '}';
    }
}
