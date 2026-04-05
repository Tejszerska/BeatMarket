package com.spring.songify.infrastructure.controller.crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SongViewController {
    Map<Integer, String> database = new HashMap<>();

    @GetMapping("/view/homepage")
    public String home(){
        return "homepage";
    }

    @GetMapping("/view/songs")
    public String songs( Model model){
        database.put(1, "Hells Bells");
        database.put(2, "Yellow Submarine");
        database.put(3, "Shallow");
        database.put(4, "Stitches");

        model.addAttribute("songMap", database);

        return "songs";
    }
}
