package com.xwikisas.eesc;

public class Group
{
    String id;

    String name;

    GroupType type;

    public Group(String groupId, String groupName, GroupType groupType)
    {
        id = groupId;
        name = groupName;
        type = groupType;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public GroupType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return String.format("Group [id=%s, name=%s, type=%s]", id, name, type);
    }
}
