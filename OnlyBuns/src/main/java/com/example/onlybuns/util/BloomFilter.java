package com.example.onlybuns.util;

import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.List;

@Component
public class BloomFilter {

    private final int size;
    private final int numHashes;
    private final BitSet bitSet;

    @Autowired
    private UserRepository userRepository;

    public BloomFilter() {
        int expectedElements = 1000;       // n
        double falsePositiveRate = 0.01;   // p

        this.size = (int) Math.ceil(-(expectedElements * Math.log(falsePositiveRate)) / (Math.pow(Math.log(2), 2)));  // m
        this.numHashes = (int) Math.ceil((double) size / expectedElements * Math.log(2));  // k
        this.bitSet = new BitSet(size);

        System.out.println("Bloom filter initialized with size: " + size + ", hashes: " + numHashes);
    }

    @PostConstruct
    public void init() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            this.add(user.getUsername());
        }
    }

    public void add(String value) {
        for (int i = 0; i < numHashes; i++) {
            int hash = hash(value, i);
            bitSet.set(Math.abs(hash % size), true);
        }
    }

    public boolean mightContain(String value) {
        for (int i = 0; i < numHashes; i++) {
            int hash = hash(value, i);
            if (!bitSet.get(Math.abs(hash % size))) {
                return false;
            }
        }
        return true;
    }

    private int hash(String value, int seed) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = seed + ":" + value;
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            int hash = 0;
            for (int i = 0; i < 4; i++) {
                hash <<= 8;
                hash |= (hashBytes[i] & 0xFF);
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
