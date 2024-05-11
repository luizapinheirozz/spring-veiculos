package com.veic.Veiculos.Santana.services;

import com.veic.Veiculos.Santana.models.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculosRepository extends JpaRepository<Veiculo, Integer> {
}
