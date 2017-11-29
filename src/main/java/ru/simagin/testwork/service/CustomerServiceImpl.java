package ru.simagin.testwork.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.simagin.testwork.data.entity.Customer;
import ru.simagin.testwork.data.dto.CustomerDTO;
import ru.simagin.testwork.data.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private Page<CustomerDTO> convertCustomerPageToDTO(Page<Customer> page) {
        return page.map((customer) -> {
            long id = customer.getId();
            StringBuilder sb = new StringBuilder();
            sb.append(customer.getLastName()).append(" ").append(customer.getFirstName());
            return new CustomerDTO(id, sb.toString());
        });
    }


    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Pageable pageable) {
        Page<Customer> customers = this.customerRepository.findAll(pageable);
        return convertCustomerPageToDTO(customers);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCriteria(String value, Pageable pageable) {
        Page<Customer> customers = this.customerRepository.findByLastNameStartsWithIgnoreCase(value, pageable);
        return convertCustomerPageToDTO(customers);
    }
}
