package com.example.demo.repository;

import com.example.demo.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
}