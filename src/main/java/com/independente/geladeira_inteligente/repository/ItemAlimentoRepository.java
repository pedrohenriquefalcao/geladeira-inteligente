package com.independente.geladeira_inteligente.repository;

import com.independente.geladeira_inteligente.model.ItemAlimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemAlimentoRepository extends JpaRepository<ItemAlimentoModel, Long> {
}
