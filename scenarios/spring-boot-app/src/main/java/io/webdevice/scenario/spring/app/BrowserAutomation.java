package io.webdevice.scenario.spring.app;

import io.webdevice.device.WebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrowserAutomation
        implements CommandLineRunner {
    private final WebDevice browser;

    @Autowired
    public BrowserAutomation(WebDevice browser) {
        this.browser = browser;
    }

    @Override
    public void run(String... args) {
        browser.home();
        browser.navigateTo("/tasks");
    }

    public static void main(String[] args) {
        SpringApplication.run(BrowserAutomation.class, args);
    }
}
