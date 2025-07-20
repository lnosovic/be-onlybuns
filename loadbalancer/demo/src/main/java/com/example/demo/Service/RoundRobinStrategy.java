package com.example.demo.Service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component("roundRobin") // Dajemo mu ime da ga lakše injektujemo
public class RoundRobinStrategy implements com.example.demo.Service.LoadBalancingStrategy {

    // Koristimo AtomicInteger da bismo bili sigurni u višenitnom okruženju
    // (kada više korisnika istovremeno šalje zahteve)
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String getNextInstance(List<String> instances) {
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("Nema dostupnih instanci servera za balansiranje.");
        }

        // Formula za Round Robin: (trenutni_brojač) % (broj_instanci)
        int index = counter.getAndIncrement() % instances.size();
        return instances.get(index);
    }
}