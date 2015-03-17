package com.xwikisas.eesc;

public class User
{
    String id;

    String name;

    UserStatus status;
    
    String etabId;

    public User(String userId, String userName, UserStatus userStatus, String userEtabId)
    {
        id = userId;
        name = userName;
        status = userStatus;
        etabId = userEtabId;
    }

    public User(String userId, String userName, String userStatus, String userEtabId)
    {
        id = userId;
        name = userName;
        status = UserStatus.forName(userStatus);
        etabId = userEtabId;
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
    
    public String getEtabId()
    {
    	return etabId;
    }

    @Override
    public String toString()
    {
        return String.format("User [id=%s, name=%s, status=%s]", id, name, status);
    }
}
