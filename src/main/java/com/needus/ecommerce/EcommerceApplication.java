package com.needus.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@Slf4j
public class EcommerceApplication {


	public static void main(String[] args) {

        SpringApplication.run(EcommerceApplication.class, args);
//     System.out.println(" ____  _____   ________   ________   ______     _____  _____    ______       \n" +
//         "|_   \\|_   _| |_   __  | |_   __  | |_   _ `.  |_   _||_   _| .' ____ \\      \n" +
//         "  |   \\ | |     | |_ \\_|   | |_ \\_|   | | `. \\   | |    | |   | (___ \\_|     \n" +
//         "  | |\\ \\| |     |  _| _    |  _| _    | |  | |   | '    ' |    _.____`.      \n" +
//         " _| |_\\   |_   _| |__/ |  _| |__/ |  _| |_.' /    \\ \\__/ /    | \\____) |  _  \n" +
//         "|_____|\\____| |________| |________| |______.'      `.__.'      \\______.' (_) \n" +
//         "                                                                             ");
//     System.out.println();
//     System.out.println("     _______. _______ .______     ____    ____  _______ .______              _______.___________.    ___      .______     .___________. _______  _______  \n" +
//         "    /       ||   ____||   _  \\    \\   \\  /   / |   ____||   _  \\            /       |           |   /   \\     |   _  \\    |           ||   ____||       \\ \n" +
//         "   |   (----`|  |__   |  |_)  |    \\   \\/   /  |  |__   |  |_)  |          |   (----`---|  |----`  /  ^  \\    |  |_)  |   `---|  |----`|  |__   |  .--.  |\n" +
//         "    \\   \\    |   __|  |      /      \\      /   |   __|  |      /            \\   \\       |  |      /  /_\\  \\   |      /        |  |     |   __|  |  |  |  |\n" +
//         ".----)   |   |  |____ |  |\\  \\----.  \\    /    |  |____ |  |\\  \\----.   .----)   |      |  |     /  _____  \\  |  |\\  \\----.   |  |     |  |____ |  '--'  |\n" +
//         "|_______/    |_______|| _| `._____|   \\__/     |_______|| _| `._____|   |_______/       |__|    /__/     \\__\\ | _| `._____|   |__|     |_______||_______/ \n" +
//         "                                                                                                                                                          ");
//	}
//
//    @PostConstruct
//    public void initTwilio(){
//        Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
//
    }
}
