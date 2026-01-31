package com.java.doc_process;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocProcessApplication.class, args);
	}

}
