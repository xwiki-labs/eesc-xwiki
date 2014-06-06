package com.xwikisas.eesc.xwiki;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.securityfilter.realm.SimplePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.extension.distribution.internal.DistributionManager;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.user.api.XWikiUser;
import com.xpn.xwiki.user.impl.xwiki.XWikiAuthServiceImpl;
import com.xpn.xwiki.web.Utils;
import com.xwikisas.eesc.EESC;
import com.xwikisas.eesc.Group;
import com.xwikisas.eesc.User;

/**
 * XWikiCASAuthServiceImpl.
 * 
 * @version $Id$
 */
public class XWikiCASAuthServiceImpl extends XWikiAuthServiceImpl
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XWikiCASAuthServiceImpl.class);

    private static final String NO_CAS_USERNAME_COOKIE = "NO_CAS_USERNAME";

    private static final String NO_CAS_PASSWORD_COOKIE = "NO_CAS_PASSWORD";

    private static final String ENT_TICKET = "ENT_TICKET";

    private static final String ENT_USERID = "ENT_USERID";

    private static final String ENT_USERGROUPES = "ENT_USERGROUPES";

    private static final String ENT_USERTYPE = "ENT_USERTYPE";

    private EESC eesc;

    public XWikiCASAuthServiceImpl() throws ComponentLookupException
    {
        eesc = Utils.getComponentManager().getInstance(EESC.class);
    }

    @Override
    public XWikiUser checkAuth(XWikiContext context) throws XWikiException
    {
        Principal principal = authenticate(null, null, context);
        if (principal != null) {
            context.setUser(principal.getName());
            return new XWikiUser(principal.getName());
        }

        return super.checkAuth(context);
    }

    @Override
    public XWikiUser checkAuth(String username, String password, String rememberme, XWikiContext context)
        throws XWikiException
    {
        Principal principal = authenticate(null, null, context);
        if (principal != null) {
            context.setUser(principal.getName());
            return new XWikiUser(principal.getName());
        }

        return super.checkAuth(username, password, rememberme, context);
    }

    @Override
    public Principal authenticate(String username, String password, XWikiContext context) throws XWikiException
    {
        HttpServletRequest request = context.getRequest().getHttpServletRequest();

        /*
         * This is a mechanism to bypass CAS authentication and switch back to standard one. Useful for initializing and
         * administering the wiki. You should have the password for the Admin account in your cookie
         */
        Cookie noCasUsernameCookie = context.getRequest().getCookie(NO_CAS_USERNAME_COOKIE);
        Cookie noCasPasswordCookie = context.getRequest().getCookie(NO_CAS_PASSWORD_COOKIE);
        if (noCasUsernameCookie != null && noCasPasswordCookie != null) {
            return super.authenticate(noCasUsernameCookie.getValue(), noCasPasswordCookie.getValue(), context);
        }

        HttpSession session = request.getSession();
        String ticket = session.getAttribute(ENT_TICKET).toString();
        String userId = session.getAttribute(ENT_USERID).toString();
        String userGroupes = session.getAttribute(ENT_USERGROUPES).toString();
        String userType = session.getAttribute(ENT_USERTYPE).toString();
        if (userId == null) {
            return super.authenticate(username, password, context);
        }

        String casID = context.getWiki().clearName(userId, true, true, context);
        String userID = eesc.getUID(casID);

        DistributionManager distributionManager = Utils.getComponent(DistributionManager.class);
        DistributionManager.DistributionState state = distributionManager.getFarmDistributionState();

        if (!DistributionManager.DistributionState.NEW.equals(state)) {
            /* If the user doesn't exist, create it. We do this only if the main wiki has been setup */
            XWikiDocument userdoc = 
                context.getWiki().getDocument(new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, userID),
                    context);
            if (userdoc.isNew()) {
                LOGGER.info("Creating user " + userID);
                context.getWiki().createEmptyUser(userID, "edit", context);
                initUser(userID, context);

            }
        }

        return new SimplePrincipal("xwiki:XWiki." + userID);
    }

    private boolean initUser(String userID, XWikiContext context) throws XWikiException
    {
        User user = eesc.getUser(userID);
        List<Group> groups = eesc.getGroupsForUser(userID);
        DocumentReference userDocRef = new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, userID);
        XWikiDocument userDoc = context.getWiki().getDocument(userDocRef, context);
        DocumentReference userObjRef = new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, "XWikiUsers");
        BaseObject userObj = userDoc.getXObject(userObjRef);

        userDoc.setHidden(true);

        userObj.set("first_name", user.getName(), context);
        userObj.set("last_name", "", context);
        context.getWiki().saveDocument(userDoc, context);

        // Add the user to the authorized users of XWiki (adding in XWikiAllGroup)
        Group xwikiAllGroup = new Group("XWikiAllGroup", "Tout le monde", "PUBLIC");
        addUserToGroup(user, xwikiAllGroup, context);

        // Add the user to the groups he's in
        for (Group group : groups) {
            addUserToGroup(user, group, context);
        }

        return true;
    }

    private void addUserToGroup(User user, Group group, XWikiContext context) throws XWikiException
    {
        DocumentReference groupDocRef =
            new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, group.getId());
        XWikiDocument groupDoc = context.getWiki().getDocument(groupDocRef, context);
        DocumentReference groupObjRef =
            new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, "XWikiGroups");
        String userPageName = String.format("XWiki.%s", user.getId());
        BaseObject groupObj = groupDoc.getXObject(groupObjRef, "member", userPageName);

        if (groupObj == null) {
            groupObj = groupDoc.newXObject(groupObjRef, context);
            groupObj.set("member", userPageName, context);
            groupDoc.setHidden(true);
            groupDoc.setTitle(group.getName());
            context.getWiki().saveDocument(groupDoc, context);
        }
    }
}
