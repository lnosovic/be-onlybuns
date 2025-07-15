package com.example.onlybuns.repository;

import com.example.onlybuns.model.RabbitCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RabbitCareRepository extends JpaRepository<RabbitCare,Integer> {
}
