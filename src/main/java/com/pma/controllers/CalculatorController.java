package com.pma.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping("/load-dataset")
    @ResponseBody
    public String loadDataset(@RequestParam("dataset") String dataset) {
        String filename = "";
        switch(dataset.toLowerCase()) {
            case "sp500":
                filename = "data/sp500_returns.txt";
                break;
            case "msci":
                filename = "data/msci_world_returns.txt";
                break;
            case "nasdaq":
                filename = "data/nasdaq_100_returns.txt";
                break;
            default:
                return "error:Unknown dataset";
        }

        try {
            ClassPathResource resource = new ClassPathResource(filename);
            String fileContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return fileContent;
        } catch (IOException e) {
            return "error:Failed to load dataset - " + e.getMessage();
        }
    }

    @PostMapping("/upload-dataset")
    @ResponseBody
    public String uploadDataset(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "error:File is empty";
        }

        try {
            String fileContent = new String(file.getBytes());
            return fileContent;
        } catch (IOException e) {
            return "error:Failed to read file - " + e.getMessage();
        }
    }
}
