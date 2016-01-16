package com.deem.excord.util;

import com.deem.excord.domain.EcUser;
import com.deem.excord.repository.UserRepository;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LdapAuth {

    @Value("${ldap.url}")
    String ldapUrl;
    @Value("${ldap.domain}")
    String ldapDomain;
    @Value("${ldap.auth}")
    Boolean ldapAuth;

    @Autowired
    UserRepository uDao;

    DirContext ctx = null;
    private final static Logger LOGGER = LoggerFactory.getLogger(LdapAuth.class);

    public boolean authenticateUser(String username, String password) {

        if (ldapAuth) {
            String[] domainArr = ldapDomain.split(",");
            for (String domain : domainArr) {
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, ldapUrl);
                env.put(Context.SECURITY_AUTHENTICATION, "simple");
                env.put(Context.SECURITY_PRINCIPAL, domain + "\\" + username);
                env.put(Context.SECURITY_CREDENTIALS, password);
                try {
                    ctx = new InitialDirContext(env);
                    return true;
                } catch (NamingException e) {

                } finally {
                    if (ctx != null) {
                        try {
                            ctx.close();
                        } catch (NamingException ex) {
                            LOGGER.warn(ex.getMessage());
                        }
                    }
                }
            }
            return false;
        } else {
            //DB authentication
            EcUser user = uDao.findByUsernameAndPassword(username, password);
            if (user == null) {
                return false;
            } else {
                return true;
            }
        }

    }

}
