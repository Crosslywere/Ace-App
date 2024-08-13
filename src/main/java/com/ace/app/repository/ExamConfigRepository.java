package com.ace.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ace.app.entity.ExportConfig;

public interface ExamConfigRepository extends JpaRepository<ExportConfig, Byte> {
}
