package com.xwikisas.eesc;

public class User
{
    String id;

    String name;

    UserStatus status;

    public User(String userId, String userName, UserStatus userStatus)
    {
        id = userId;
        name = userName;
        status = userStatus;
    }

    public User(String userId, String userName, String userStatus)
    {
        id = userId;
        name = userName;
        status = UserStatus.forName(userStatus);
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public UserStatus getStatus()
    {
        return status;
    }

    @Override
    public String toString()
    {
        return String.format("User [id=%s, name=%s, status=%s]", id, name, status);
    }
}
