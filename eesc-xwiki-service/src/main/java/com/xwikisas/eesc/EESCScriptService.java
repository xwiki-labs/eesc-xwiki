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

import com.xwikisas.eesc.internal.EESCImpl;

@Component
@Named("eesc")
public class EESCScriptService implements ScriptService, Initializable
{
    @Inject
    ConfigurationSource configurationSource;

    @Inject
    ComponentManager componentManager;

    EESC eesc;

    public String getUID(String casID)
    {
        return eesc.getUID(casID);
    }

    public User getUser(String userID)
    {
        return eesc.getUser(userID);
    }

    public Group getGroup(String groupID)
    {
        return eesc.getGroup(groupID);
    }

    public List<User> getUsersForGroup(String groupID)
    {
        return eesc.getUsersForGroup(groupID);
    }

    public List<Group> getGroupsForUser(String userID)
    {
        return eesc.getGroupsForUser(userID);
    }

    public boolean isMember(String userID, String groupID)
    {
        return eesc.isMember(userID, groupID);
    }

    public List<Group> getAllGroups()
    {
        return eesc.getAllGroups();
    }

    @Override
    public void initialize() throws InitializationException
    {
        String eescService = configurationSource.getProperty("eesc.service");
        String eescWebservice = configurationSource.getProperty("eesc.webservice.url");
        try {
            if (eescService != null) {
                eesc = componentManager.getInstance(EESC.class, eescService);
            } else {
                eesc = componentManager.getInstance(EESC.class);
                if (eescWebservice != null) {
                    ((EESCImpl) eesc).setServiceURL(eescWebservice);
                }
            }
        } catch (Exception e) {
            throw new InitializationException("Error initializing EESC Script Service", e);
        }
    }

}
