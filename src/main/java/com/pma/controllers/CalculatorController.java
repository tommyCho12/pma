package com.pma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/calculator")
public class CalculatorController
{

    @GetMapping
    public String display(Model model){
        ClassPathResource resource = new ClassPathResource("data/sp500_returns.txt");
        String fileContent = "";
        try
        {
            fileContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        model.addAttribute("initialData", fileContent);
        return "calculator/home";
    }
}
