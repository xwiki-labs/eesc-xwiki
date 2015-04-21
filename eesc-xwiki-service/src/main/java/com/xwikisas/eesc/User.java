package com.xwikisas.eesc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User
{
    String id;

    String name;

    UserStatus status;
    
    List<String> etabId;

    public User(String userId, String userName, UserStatus userStatus, List<String> userEtabId)
    {
        id = userId;
        name = userName;
        status = userStatus;
        etabId = userEtabId;
    }

    public User(String userId, String userName, String userStatus, List<String> userEtabId)
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
    
    public List<String> getEtabId()
    {
    	return etabId;
    }

    @Override
    public String toString()
    {
        return String.format("User [id=%s, name=%s, status=%s, etab=%s]", id, name, status, etabId);
    }
}
