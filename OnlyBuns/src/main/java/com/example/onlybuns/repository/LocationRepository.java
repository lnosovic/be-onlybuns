package com.example.onlybuns.repository;

import com.example.onlybuns.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location,Integer> {
}
