package com.xwikisas.eesc;

public enum UserStatus
{
    STUDENT("student"),
    TEACHER("teacher"),
    PARENT("parent"),
    ADMIN("admin");

    /** Name that is used as a descriptor of the property. */
    private final String name;

    /**
     * @param name The string name corresponding to the user status.
     */
    UserStatus(String name)
    {
        this.name = name;
    }

    /**
     * @return The string name corresponding to the user status.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param name The string name corresponding to the user status.
     * @return The user status corresponding to the parameter 'name'.
     */
    public static UserStatus forName(String name)
    {
        for (UserStatus type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
