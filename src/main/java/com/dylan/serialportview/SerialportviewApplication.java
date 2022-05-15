package com.dylan.serialportview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SerialportviewApplication {

    public static void main(String[] args) {
        Thread thread = new Thread(new Task());
        SpringApplication.run(SerialportviewApplication.class, args);
        thread.start();

    }

}
