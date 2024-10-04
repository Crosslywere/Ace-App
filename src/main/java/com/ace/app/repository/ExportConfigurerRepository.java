package com.ace.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.ExportConfigurer;

/**
 * @author Ogboru Jude
 * @version 0.0.1-SNAPSHOT
 */
@Repository
public interface ExportConfigurerRepository extends JpaRepository<ExportConfigurer, Integer> {
}
