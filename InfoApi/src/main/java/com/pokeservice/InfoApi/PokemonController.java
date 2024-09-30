package com.pokeservice.InfoApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/pokemon")
@CrossOrigin()
public class PokemonController {
    private final PokemonService pokemonService;

    @Autowired
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public ResponseEntity<String> getPokemons(@RequestParam(defaultValue = "0" ) int offset,
                                                                   @RequestParam(defaultValue = "5") int limit ) {
        return ResponseEntity.ok( pokemonService.getPokemonList(offset, limit) );
    }

    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<String> getPokemon(@PathVariable(value = "id") int id){
        return  ResponseEntity.ok( pokemonService.getPokemon(id) );
    }
}
