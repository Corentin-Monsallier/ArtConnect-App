package com.project.artconnect.util;

import com.project.artconnect.model.UserRole;

public class SessionManager {

    private static SessionManager instance;

    private int      userId;
    private String   userName;
    private String   email;
    private UserRole role = UserRole.VIEWER;
    private String   pendingEmail = "";

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(int userId, String userName, String email, UserRole role) {
        this.userId = userId; this.userName = userName;
        this.email = email; this.role = role;
    }

    public void logout() {
        userId = 0; userName = null; email = null; role = UserRole.VIEWER;
    }

    public boolean isViewer()   { return role == UserRole.VIEWER; }
    public boolean isMember()   { return role == UserRole.MEMBER; }
    public boolean isArtist()   { return role == UserRole.ARTIST; }
    public boolean isAdmin()    { return role == UserRole.ADMIN; }
    public boolean isLoggedIn() { return role != UserRole.VIEWER; }

    public int      getUserId()    { return userId; }
    public String   getUserName()  { return userName; }
    public String   getEmail()     { return email; }
    public UserRole getRole()      { return role; }

    public String getPendingEmail()             { return pendingEmail == null ? "" : pendingEmail; }
    public void   setPendingEmail(String email) { this.pendingEmail = email; }
}