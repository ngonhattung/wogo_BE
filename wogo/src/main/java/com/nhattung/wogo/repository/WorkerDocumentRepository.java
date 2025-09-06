package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerDocument;
import io.micrometer.common.KeyValues;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerDocumentRepository extends JpaRepository<WorkerDocument, Long> {
}
