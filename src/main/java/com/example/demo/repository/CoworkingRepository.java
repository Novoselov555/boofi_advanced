package com.example.demo.repository;

import com.example.demo.entity.Coworking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoworkingRepository extends JpaRepository<Coworking, Long> {
}
