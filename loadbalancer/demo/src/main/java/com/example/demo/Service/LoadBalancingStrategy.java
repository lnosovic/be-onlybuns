package com.example.demo.Service;

import java.util.List;

public interface LoadBalancingStrategy {
    String getNextInstance(List<String> instances);
}