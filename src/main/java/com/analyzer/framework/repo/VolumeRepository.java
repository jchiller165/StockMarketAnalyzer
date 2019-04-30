package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.Volume;

public interface VolumeRepository extends JpaRepository<Volume, String> {

}
