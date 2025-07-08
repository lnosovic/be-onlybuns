package com.example.onlybuns.scheduler;

import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.model.User;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Za rad sa datumima
import java.util.List;

@Component
public class AccountCleanupScheduler {

    private final UserRepository userRepository; // Deklaracija repozitorijuma

    // Konstruktor za Dependency Injection (Spring će automatski ubaciti UserRepository)
    public AccountCleanupScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //@Scheduled(cron = "0 * * * * ?")    //every minute
    @Scheduled(cron = "0 0 0 L * ?") //00:00 last day of month
    @Transactional
    public void deleteInactiveAccounts() {
        System.out.println("Pokrenut zakazani zadatak za brisanje neaktivnih naloga: " + LocalDateTime.now());


        List<User> inactiveUsers = userRepository.findByIsActivatedFalse();

        if (inactiveUsers.isEmpty()) {
            System.out.println("Nema neaktivnih naloga za brisanje.");
            return; // Nema šta da se briše
        }

        System.out.println("Pronađeno " + inactiveUsers.size() + " neaktivnih naloga za brisanje.");

        userRepository.deleteAll(inactiveUsers); // Briše sve korisnike iz liste

        System.out.println("Uspešno obrisano " + inactiveUsers.size() + " neaktivnih naloga.");
    }
}