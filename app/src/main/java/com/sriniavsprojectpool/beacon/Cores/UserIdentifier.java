package com.sriniavsprojectpool.beacon.Cores;

public class UserIdentifier {
    private String email;
    private String userFlag;

    public UserIdentifier(String email) {
        this.email = email;
    }

    public String getUserFlag() {
        if(email.contains("@sreenidhi.edu.in")&&email.charAt(0)=='1')
        {
            userFlag = "Student";
        }
        else if(email.contains("@sreenidhi.edu.in")&&email.charAt(0)!='1')
        {
            userFlag = "Management";
        }
        else
        {
            userFlag = "Guest";
        }
        return userFlag;
    }
}
