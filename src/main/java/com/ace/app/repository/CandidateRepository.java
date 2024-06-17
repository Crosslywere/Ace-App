package com.ace.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.app.entity.Candidate;
import com.ace.app.model.CandidateId;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, CandidateId> {
}
