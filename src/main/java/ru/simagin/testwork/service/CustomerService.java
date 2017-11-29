package ru.simagin.testwork.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.simagin.testwork.data.dto.CustomerDTO;

public interface CustomerService {
    Page<CustomerDTO> findAll(Pageable pageable);

    Page<CustomerDTO> findByCriteria(String value, Pageable pageable);
}
