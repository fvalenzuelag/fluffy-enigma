package com.example.cartas.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cartas.model.Carta;

public interface CartaRepository extends JpaRepository<Carta, Long> {
}
