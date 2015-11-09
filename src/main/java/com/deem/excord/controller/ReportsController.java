package com.deem.excord.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ReportsController {

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public String reportsHome(Model model) {
        return "reports";
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help(Model model) {
        return "help";
    }

}
