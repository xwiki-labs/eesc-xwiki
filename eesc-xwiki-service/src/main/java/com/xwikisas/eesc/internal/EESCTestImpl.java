package com.xwikisas.eesc.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.environment.Environment;

import com.xwikisas.eesc.EESC;
import com.xwikisas.eesc.Group;
import com.xwikisas.eesc.GroupType;
import com.xwikisas.eesc.User;
import com.xwikisas.eesc.UserStatus;

@Component("test")
public class EESCTestImpl implements EESC, Initializable
{
    @Inject
    Environment environment;

    private Properties properties;

    private String getUserName(String userId)
    {
        String userKey = String.format("user.%s", userId);
        return properties.getProperty(userKey);
    }

    private UserStatus getUserStatus(String userId)
    {
        String userKey = String.format("user.%s.status", userId);
        return UserStatus.valueOf(properties.getProperty(userKey));

    }

    private String getGroupName(String groupId)
    {
        String groupKey = String.format("group.%s", groupId);
        return properties.getProperty(groupKey);
    }

    private GroupType getGroupType(String groupId)
    {
        String groupKey = String.format("group.%s.type", groupId);
        return GroupType.valueOf(properties.getProperty(groupKey));

    }


    @Override
    public User getUser(String userId)
    {
        User user = null;
        if(userId != null) {
            String userName = getUserName(userId);
            UserStatus userStatus = getUserStatus(userId);
            user = new User(userId, userName, userStatus);
        }
        return user;
    }

    @Override
    public Group getGroup(String groupId)
    {
        Group group = null;
        if(groupId != null) {
            String groupName = getGroupName(groupId);
            GroupType groupType = getGroupType(groupId);
            group = new Group(groupId, groupName, groupType);
        }
        return group;
    }
    
    @Override
    public List<User> getUsersForGroup(String groupId)
    {
        String prefix = String.format("group.%s.user.", groupId);
        List<User> userList = new ArrayList<User>();
        if (groupId != null) {
            for (Object o : properties.keySet()) {
                String key = (String) o;

                if (key.startsWith(prefix)) {
                    String userId = key.split("\\.")[3];
                    String userName = getUserName(userId);
                    UserStatus userStatus = getUserStatus(userId);
                    User user = new User(userId, userName, userStatus);
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    @Override
    public List<Group> getGroupsForUser(String userId)
    {
        List<Group> groupList = new ArrayList<Group>();
        if (userId != null) {
            for (Object o : properties.keySet()) {
                String key = (String) o;
                if (key.startsWith("group.") && key.endsWith(userId)) {
                    String groupId = key.split("\\.")[1];
                    String groupName = getGroupName(groupId);
                    GroupType groupType = getGroupType(groupId);
                    Group group = new Group(groupId, groupName, groupType);
                    groupList.add(group);
                }
            }
        }
        return groupList;
    }

    @Override
    public boolean isMember(String userId, String groupId)
    {
        return properties.getProperty(String.format("group.%s.%s", groupId, userId)) != null;
    }

    @Override
    public void initialize() throws InitializationException
    {
        URL resource = environment.getResource("/WEB-INF/eesc.properties");
        properties = new Properties();
        try {
            if (resource != null) {
                properties.load(resource.openStream());
            }
        } catch (Exception e) {
            throw new InitializationException("Error initializing EESC Test implementation", e);
        }

    }

}
