package pl.dev_software.saunamaster.toolbox.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CommunicationTestController {

    @GetMapping("/test")
    public Map<String, String> testConnection() {
        return Map.of("message", "Hello World!");
    }
    
}