package com.qings.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by qings on 2017/7/17.
 */
@Controller
public class IndexController {


    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
