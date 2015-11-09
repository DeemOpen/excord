package com.deem.excord.controller;

import com.deem.excord.domain.EcUser;
import com.deem.excord.repository.UserRepository;
import com.deem.excord.util.Constants;
import com.deem.excord.util.LdapAuth;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LdapAuth authUtil;

    @Autowired
    UserRepository uDao;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("loginMessage", "Login with your Outlook account, (No Domain name needed)");
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(HttpSession session, @RequestParam(value = "username", required = true) String username, @RequestParam(value = "password", required = true) String password, Model model) {

        session.setMaxInactiveInterval(6000);
        if (authUtil.authenticateUser(username, password)) {
            logger.info("Successfull login: {}", username);
            if (uDao.findByUsername(username) == null) {
                logger.info("First time login: {}", username);
                EcUser user = new EcUser();
                user.setEnabled(true);
                user.setCreatedDate(new Date());
                user.setRole(Constants.ROLE_USER);
                user.setUsername(username);
                uDao.save(user);
            }
            session.setAttribute("authName", username);
            session.setAttribute("authRole", "user");
            String nextUrl = (String) session.getAttribute("nextUrl");
            logger.info("Next Url: {}", nextUrl);
            if (nextUrl != null && !nextUrl.equals("/login")) {
                return "redirect:" + nextUrl;
            } else {
                return "redirect:/";
            }
        } else {
            logger.info("Invalid login: {}", username);
            model.addAttribute("flashMsg", "Invalid Login!");
            return "login";
        }

    }

}
