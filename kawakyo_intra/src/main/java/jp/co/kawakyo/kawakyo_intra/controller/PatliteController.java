package jp.co.kawakyo.kawakyo_intra.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatliteController {
    
    @GetMapping("/patlite")
    public String patliteCall() {
        return "patliteCall";
    }

}
