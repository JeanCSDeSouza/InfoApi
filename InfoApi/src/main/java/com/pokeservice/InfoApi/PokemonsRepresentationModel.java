package com.pokeservice.InfoApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class PokemonsRepresentationModel extends RepresentationModel<PokemonsRepresentationModel> implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private String pokemonData;
}
