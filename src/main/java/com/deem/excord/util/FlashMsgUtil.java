package com.deem.excord.util;

import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;

public enum FlashMsgUtil {

    INSTANCE;

    public void checkFlashMsg(HttpSession session, Model model) {
        if (session.getAttribute("flashMsg") != null) {
            model.addAttribute("flashMsg", session.getAttribute("flashMsg"));
            session.setAttribute("flashMsg", null);
        }
    }

}
