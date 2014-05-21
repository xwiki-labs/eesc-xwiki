package com.xwikisas.eesc;

public enum GroupType
{
    PRIVATE("private"),
    RESTRICTED("restrict"),
    PUBLIC("public");

    /** Name that is used as a descriptor of the property. */
    private final String name;

    /**
     * @param name The string name corresponding to the group type.
     */
    GroupType(String name)
    {
        this.name = name;
    }

    /**
     * @return The string name corresponding to the group type.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param name The string name corresponding to the group type.
     * @return The group type corresponding to the parameter 'name'.
     */
    public static GroupType forName(String name)
    {
        for (GroupType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
