package com.veic.Veiculos.Santana.services;

import com.veic.Veiculos.Santana.models.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface VeiculosRepository extends JpaRepository<Veiculo, Integer> {
    List<Veiculo> findByModeloOrAno(String modelo, String ano);

    @Query("SELECT DISTINCT v.categoria FROM Veiculo v")
    Set<String> findDistinctCategorias();

    List<Veiculo> findByCategoria(String categoria);
}
