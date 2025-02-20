package com.mycompany.jobhunter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MainApplication {

    private static final Logger logger = LogManager.getLogger(MainApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        logger.debug("doStuff needed to debug - {}", 1);
        logger.info("doStuff took input - {}", 1);
        logger.warn("doStuff needed to warn - {}", 2);
        logger.error("doStuff encountered an error with value - {}", 2);
    }

}
