package edu.ucsb.cs.cs190i.aferguson.ripple;

/**
 * Created by shadeebarzin on 6/4/17.
 */

class User {
    private String userId;
    private String name;
    private String photoUrl;

    private User() {}

    public User(String userId, String name, String photoUrl) {
        this.userId = userId;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return userId + " " + name + " " + photoUrl;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhotoUrl() { return photoUrl; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
