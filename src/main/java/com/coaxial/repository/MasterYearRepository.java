package com.coaxial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.MasterYear;

@Repository
public interface MasterYearRepository extends JpaRepository<MasterYear, Long> {
    List<MasterYear> findByIsActiveTrueOrderByYearValueAsc();
    List<MasterYear> findAllByOrderByYearValueDesc();
    java.util.Optional<MasterYear> findByYearValue(Integer yearValue);
}