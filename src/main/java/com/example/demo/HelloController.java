package com.example.demo;

import com.example.demo.dto.ValuteCursOnDate;
import com.example.demo.service.CentralRussianBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final CentralRussianBankService centralRussianBankService;

    // Inject CentralRussianBankService using constructor-based dependency injection
    @Autowired
    public HelloController(CentralRussianBankService centralRussianBankService) {
        this.centralRussianBankService = centralRussianBankService;
    }

    //127.0.0.1:8080/api/hello
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }

    //127.0.0.1:8080/api/cbr
    @GetMapping("/cbr")
    public String helloCbr() {
        try {
            List<ValuteCursOnDate> listCurrencies = centralRussianBankService.getCurrenciesFromCbr();
            return "ok cbr";
        } catch (Exception exception) {
            return "Error: " + exception.getMessage();
        }
    }
}
