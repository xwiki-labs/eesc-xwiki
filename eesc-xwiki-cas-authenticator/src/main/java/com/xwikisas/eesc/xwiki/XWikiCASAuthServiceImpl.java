package com.xwikisas.eesc.xwiki;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.securityfilter.realm.SimplePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.extension.distribution.internal.DistributionManager;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.user.api.XWikiUser;
import com.xpn.xwiki.user.impl.xwiki.XWikiAuthServiceImpl;
import com.xpn.xwiki.web.Utils;

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

    @Override public XWikiUser checkAuth(XWikiContext context) throws XWikiException
    {
        Principal principal = authenticate(null, null, context);
        if (principal != null) {
            context.setUser(principal.getName());
            return new XWikiUser(principal.getName());
        }

        return super.checkAuth(context);
    }

    @Override public XWikiUser checkAuth(String username, String password, String rememberme, XWikiContext context)
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
    public Principal authenticate(String username, String password, XWikiContext context)
            throws XWikiException
    {
        HttpServletRequest request = context.getRequest().getHttpServletRequest();

        /* This is a mechanism to bypass CAS authentication and switch back to standard one. Useful for initializing
           and administering the wiki. */
        Cookie noCasCookie = context.getRequest().getCookie(NO_CAS_COOKIE);
        if (noCasCookie != null) {
            return super.authenticate(username, password, context);
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
                    context.getWiki()
                            .getDocument(new DocumentReference(context.getDatabase(), "XWiki", userWikiName), context);
            if (userdoc.isNew()) {
                LOGGER.info("Creating user " + userWikiName);
                context.getWiki().createEmptyUser(userWikiName, "edit", context);
            }
        }

        return new SimplePrincipal("xwiki:XWiki." + userWikiName);
    }
}
