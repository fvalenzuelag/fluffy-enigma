package com.example.cartas.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.cartas.model.Carta;
import com.example.cartas.repo.CartaRepository;

@RestController
@RequestMapping("/cartas")
public class CartaController {

    private final CartaRepository cartaRepository;

    public CartaController(CartaRepository cartaRepository) {
        this.cartaRepository = cartaRepository;
    }

    @GetMapping
    public List<Carta> listar() {
        return cartaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carta> obtener(@PathVariable Long id) {
        return cartaRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Carta> crear(@RequestBody Carta carta, UriComponentsBuilder uriBuilder) {
        carta.setId(null);
        Carta guardada = cartaRepository.save(carta);
        URI location = uriBuilder.path("/cartas/{id}").buildAndExpand(guardada.getId()).toUri();
        return ResponseEntity.created(location).body(guardada);
    }
}
