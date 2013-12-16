package com.xwikisas.eesc;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.script.service.ScriptService;

@Component
@Named("eesc")
public class EESCScriptService implements ScriptService, Initializable
{
    @Inject
    ConfigurationSource configurationSource;

    @Inject
    ComponentManager componentManager;

    EESC eesc;

    public User getUser(String userId)
    {
        return eesc.getUser(userId);
    }

    public Group getGroup(String groupId)
    {
        return eesc.getGroup(groupId);
    }

    public List<User> getUsersForGroup(String groupId)
    {
        return eesc.getUsersForGroup(groupId);
    }

    public List<Group> getGroupsForUser(String userId)
    {
        return eesc.getGroupsForUser(userId);
    }

    public boolean isMember(String userId, String groupId)
    {
        return eesc.isMember(userId, groupId);
    }

    @Override
    public void initialize() throws InitializationException
    {
        String eescService = configurationSource.getProperty("eesc.service");
        try {
            if (eescService != null) {
                eesc = componentManager.getInstance(EESC.class, eescService);
            } else {
                eesc = componentManager.getInstance(EESC.class);
            }
        } catch (Exception e) {
            throw new InitializationException("Error initializing EESC Script Service", e);
        }
    }

}
