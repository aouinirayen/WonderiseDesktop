package com.esprit.wonderwise.Util;

import com.esprit.wonderwise.Model.User;

public class UserSession {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
