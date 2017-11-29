package ru.simagin.testwork.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.simagin.testwork.data.entity.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByLastNameStartsWithIgnoreCase(String lastName, Pageable pageable);
}
