package com.xwikisas.eesc.xwiki;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
import com.xwikisas.eesc.User;

/**
 * XWikiCASAuthServiceImpl.
 * 
 * @version $Id$
 */
public class XWikiCASAuthServiceImpl extends XWikiAuthServiceImpl
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XWikiCASAuthServiceImpl.class);

    private static final String NO_CAS_COOKIE = "NO_CAS";

    private static final String ENT_USERID = "ENT_USERID";

    private EESC eesc;

    public XWikiCASAuthServiceImpl() throws ComponentLookupException
    {
        eesc = Utils.getComponentManager().getInstance(EESC.class, "test");
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
         * administering the wiki.
         */
        Cookie noCasCookie = context.getRequest().getCookie(NO_CAS_COOKIE);
        if (noCasCookie != null) {
            return super.authenticate("Admin", "admin", context);
        }

        String userId = (String) request.getSession().getAttribute(ENT_USERID);
        if (userId == null) {
            return super.authenticate(username, password, context);
        }

        String userWikiName = context.getWiki().clearName(userId, true, true, context);

        DistributionManager distributionManager = Utils.getComponent(DistributionManager.class);
        DistributionManager.DistributionState state = distributionManager.getFarmDistributionState();

        if (!DistributionManager.DistributionState.NEW.equals(state)) {
            /* If the user doesn't exist, create it. We do this only if the main wiki has been setup */
            XWikiDocument userdoc =
                context.getWiki().getDocument(
                    new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, userWikiName), context);
            if (userdoc.isNew()) {
                LOGGER.info("Creating user " + userWikiName);
                context.getWiki().createEmptyUser(userWikiName, "edit", context);
                initUser(userWikiName, context);

            }
        }

        return new SimplePrincipal("xwiki:XWiki." + userWikiName);
    }

    private boolean initUser(String userWikiName, XWikiContext context) throws XWikiException
    {
        DocumentReference userDocRef = new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, userWikiName);
        XWikiDocument userDoc = context.getWiki().getDocument(userDocRef, context);
        DocumentReference userObjRef = new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, "XWikiUsers");
        BaseObject userObj = userDoc.getXObject(userObjRef);

        userDoc.setHidden(true);

        User user = eesc.getUser(userWikiName);
        userObj.set("first_name", user.getName(), context);
        userObj.set("last_name", "", context);
        context.getWiki().saveDocument(userDoc, context);

        // Add the user to the authorized users of XWiki (adding in XWikiAllGroup)
        addUserToGroup(user, "XWikiAllGroup", context);

        // Add the user to the ENT groups
        addUserToGroup(user, "ENTAllGroup", context);
        switch (user.getStatus()) {
            case TEACHER:
                addUserToGroup(user, "ENTTeacher", context);
                break;
            case STUDENT:
                addUserToGroup(user, "ENTStudent", context);
                break;
            case PARENT:
                addUserToGroup(user, "ENTParent", context);
                break;
            case LOCAL_ADMIN:
                addUserToGroup(user, "ENTLocalAdmin", context);
                break;
            case STAFF:
                addUserToGroup(user, "ENTStaff", context);
                break;
            case GUEST:
                addUserToGroup(user, "ENTGuest", context);
                break;
        }

        return true;
    }

    private boolean addUserToGroup(User user, String groupName, XWikiContext context) throws XWikiException
    {
        DocumentReference groupDocRef = new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, groupName);
        XWikiDocument groupDoc = context.getWiki().getDocument(groupDocRef, context);
        DocumentReference groupObjRef =
            new DocumentReference(context.getMainXWiki(), XWiki.SYSTEM_SPACE, "XWikiGroups");
        String userPageName = String.format("XWiki.%s", user.getId());
        BaseObject groupObj = groupDoc.getXObject(groupObjRef, "member", userPageName);

        if (groupObj == null) {
            groupObj = groupDoc.newXObject(groupObjRef, context);
            groupObj.set("member", userPageName, context);
            groupDoc.setHidden(true);
            context.getWiki().saveDocument(groupDoc, context);
        }
        return true;
    }
}
