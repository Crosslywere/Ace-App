package com.ace.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.Paper;
import com.ace.app.model.PaperId;

@Repository
public interface PaperRepository extends JpaRepository<Paper, PaperId> {
}
