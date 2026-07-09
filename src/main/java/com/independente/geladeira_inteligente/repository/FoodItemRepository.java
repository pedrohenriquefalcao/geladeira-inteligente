package com.independente.geladeira_inteligente.repository;

import com.independente.geladeira_inteligente.model.FoodItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItemModel, Long> {
}
