package com.xwikisas.eesc.internal;

import java.util.List;

import org.xwiki.component.annotation.Component;

import com.xwikisas.eesc.EESC;
import com.xwikisas.eesc.Group;
import com.xwikisas.eesc.User;

@Component
public class EESCImpl implements EESC
{
    @Override
    public User getUser(String userId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group getGroup(String groupId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<User> getUsersForGroup(String groupId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Group> getGroupsForUser(String userId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isMember(String userId, String groupId)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Group> getAllGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
