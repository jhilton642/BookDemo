package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class mvcController {
    @RequestMapping(value = "/mvc")
    public String mvcCreate(Model model){
        model.addAttribute("reverseUrl", "com.example.demo");
        model.addAttribute("entity", "your class goes here");
        model.addAttribute("findBy", true);

        return "Godzilla";
    }

    @RequestMapping(value = "/mvc/create", method = RequestMethod.POST)
    public String save(@RequestParam String reverseUrl, String entity, String findBy, Model model){
        model.addAttribute("reverseUrl", reverseUrl);
        model.addAttribute("entity", entity);
        model.addAttribute("findBy", findBy);
//        model.addAttribute("noHTML", noHTML);
//        model.addAttribute("noJava", noJava);

        Godzilla.buildMVC(reverseUrl, entity.toLowerCase(),
                                findBy == null ? false : findBy.equals("true"), true, true);
//                                noHTML == null ? true : !noHTML.equals("true"),     //  i know bad logic if the checkbox is null or false create the pages / code
//                                noJava == null ? true : !noJava.equals("true"));    //  if the checkbox is true do not create the pages / code
        System.out.println("and we are done");
        return "redirect:/books/";              //  go to the book listing home page
    }
}
