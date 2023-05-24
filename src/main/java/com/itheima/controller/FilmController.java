package com.itheima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Calendar;

@Controller
public class FilmController {

    @GetMapping({"/detail/{type}/{path}"})
    public String toDetail(@PathVariable("type") String type,@PathVariable("path") int path){
        return "detail/"+type+"/"+path;
    }

    @GetMapping("/userLogin")
    public String customerLogin(Model model){
        model.addAttribute("currentYear", Calendar.getInstance().get(Calendar.YEAR));
        return "login";
    }
}
