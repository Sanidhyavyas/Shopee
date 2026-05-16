package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByFranchiseFranchiseId(Long franchiseId);

    boolean existsByNameAndFranchiseFranchiseId(String name, Long franchiseId);
}
