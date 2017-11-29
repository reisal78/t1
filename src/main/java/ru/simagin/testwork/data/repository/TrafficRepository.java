package ru.simagin.testwork.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.simagin.testwork.data.entity.Traffic;

public interface TrafficRepository extends JpaRepository<Traffic, Long>, JpaSpecificationExecutor<Traffic> {
}
