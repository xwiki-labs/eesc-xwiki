package com.xwikisas.eesc;

import java.util.List;

import org.xwiki.component.annotation.Role;

@Role
public interface EESC
{
    User getUser(String userId);

    Group getGroup(String groupId);

    List<User> getUsersForGroup(String groupId);

    List<Group> getGroupsForUser(String userId);

    boolean isMember(String userId, String groupId);

}
