package com.xwikisas.eesc.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.environment.Environment;

import com.xwikisas.eesc.EESC;
import com.xwikisas.eesc.Group;
import com.xwikisas.eesc.GroupType;
import com.xwikisas.eesc.User;
import com.xwikisas.eesc.UserStatus;

@Component
@Named("test")
public class EESCTestImpl implements EESC, Initializable
{
    @Inject
    Environment environment;

    private Properties properties;

    private String getUserName(String userID)
    {
        String userKey = String.format("user.%s", userID);
        return properties.getProperty(userKey);
    }

    private UserStatus getUserStatus(String userID)
    {
        String userKey = String.format("user.%s.status", userID);
        String property = properties.getProperty(userKey);
        UserStatus userStatus = null;
        if (property != null) {
            userStatus = UserStatus.valueOf(property);
        }
        return userStatus;

    }

    private String getGroupName(String groupID)
    {
        String groupKey = String.format("group.%s", groupID);
        return properties.getProperty(groupKey);
    }

    private GroupType getGroupType(String groupId)
    {
        String groupKey = String.format("group.%s.type", groupId);
        String property = properties.getProperty(groupKey);
        GroupType groupType = null;
        if (property != null) {
            groupType = GroupType.valueOf(property);
        }
        return groupType;

    }

    @Override
    public String getUID(String casID)
    {
        return casID;
    }

    @Override
    public User getUser(String userID)
    {
        User user = null;
        if (userID != null) {
            String userName = getUserName(userID);
            UserStatus userStatus = getUserStatus(userID);
            if (userName != null && userStatus != null) {
                user = new User(userID, userName, userStatus);
            }
        }
        return user;
    }

    @Override
    public Group getGroup(String groupID)
    {
        Group group = null;
        if (groupID != null) {
            String groupName = getGroupName(groupID);
            GroupType groupType = getGroupType(groupID);
            if (groupName != null && groupType != null) {
                group = new Group(groupID, groupName, groupType);
            }
        }
        return group;
    }

    @Override
    public List<User> getUsersForGroup(String groupID)
    {
        String prefix = String.format("group.%s.user.", groupID);
        List<User> userList = new ArrayList<User>();
        if (groupID != null) {
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
    public List<Group> getGroupsForUser(String userID)
    {
        List<Group> groupList = new ArrayList<Group>();
        if (userID != null) {
            for (Object o : properties.keySet()) {
                String key = (String) o;
                if (key.startsWith("group.") && key.endsWith(userID)) {
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
    public boolean isMember(String userID, String groupID)
    {
        return properties.getProperty(String.format("group.%s.%s", groupID, userID)) != null;
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

    @Override
    public List<Group> getAllGroups()
    {
        List<Group> groupList = new ArrayList<Group>();
        List<String> alreadyAdded = new ArrayList<String>();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            if (key.startsWith("group.")) {
                String groupId = key.split("\\.")[1];
                if (!alreadyAdded.contains(groupId)) {
                    alreadyAdded.add(groupId);
                    String groupName = getGroupName(groupId);
                    GroupType groupType = getGroupType(groupId);
                    Group group = new Group(groupId, groupName, groupType);
                    groupList.add(group);
                }
            }

        }
        return groupList;
    }
}
