package edu.ucsb.cs.cs190i.aferguson.ripple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shadeebarzin on 6/5/17.
 */

class Broadcast {

    private String id;
    private List<String> listeners;

    private Broadcast() {}

    public Broadcast(String id) {
        this.id = id;
        this.listeners = new ArrayList<>();
    }

    public Broadcast(String id, List<String> listeners) {
        this.id = id;
        this.listeners = listeners;
    }

    @Override
    public String toString() {
        return id + "num listeners: " + Integer.toString(listeners.size());
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public List<String> getListeners() {
        return listeners;
    }
    public void setListeners(List<String> listeners) {
        this.listeners = listeners;
    }
}
