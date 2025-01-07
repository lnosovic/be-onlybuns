package com.example.onlybuns.service;

import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /*
     * Koriscenje klase za ocitavanje vrednosti iz application.properties fajla
     */
    @Autowired
    private Environment env;
    @Autowired
    private UserRepository userRepository;



    public void sendNotificaitionSync(UserRequest user) throws MailException, InterruptedException {
        System.out.println("Sync metoda se izvrsava u istom Threadu koji je i prihvatio zahtev. Thread id: " + Thread.currentThread().getId());
        //Simulacija duze aktivnosti da bi se uocila razlika
        //Thread.sleep(10000);
        System.out.println("Slanje emaila...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setFrom(env.getProperty("spring.mail.username"));
        mail.setSubject("Aktivacija naloga OnlyBuns");
        User request = userRepository.findByUsername(user.getUsername());
        String link = "http://localhost:4200/activation/" + request.getId();
        mail.setText("Pozdrav " + user.getName() + ",\n\nhvala što ste se pridružili OnlyBuns.\n\n Klikni na sledeći link za aktivaciju naloga:\n" + link);
        javaMailSender.send(mail);

        System.out.println("Email poslat!");
    }
}
