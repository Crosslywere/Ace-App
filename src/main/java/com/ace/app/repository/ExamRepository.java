package com.ace.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.Exam;
import java.util.List;
import com.ace.app.model.ExamState;

/**
 * @author Ogboru Jude
 * @version 18-June-2024
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

	List<Exam> findByState( ExamState state );

	long countByState( ExamState state );

	List<Exam> findByTitleContainingIgnoreCase( String title );
}
