package com.czerniecka.customer.service;

import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.dto.CustomerMapper;
import com.czerniecka.customer.entity.Customer;
import com.czerniecka.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDTO> findAll() {

        List<Customer> all = customerRepository.findAll();
        return customerMapper.toCustomersDTOs(all);
    }

    public Optional<CustomerDTO> findCustomerById(UUID customerId) {

        Optional<Customer> byId = customerRepository.findById(customerId);
        return byId.map(customerMapper::toCustomerDTO);
    }

    public CustomerDTO save(CustomerDTO customerDTO) {

        Customer customer = customerMapper.toCustomer(customerDTO);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toCustomerDTO(saved);
    }

    public boolean updateCustomer(UUID customerId, CustomerDTO customerDTO) {

        Optional<Customer> c = customerRepository.findById(customerId);

        if(c.isPresent()){
            Customer customer = c.get();
            customer.setName(customerDTO.getName());
            customer.setEmail(customerDTO.getEmail());

            customerRepository.save(customer);
            return true;
        }else{
            return false;
        }
    }
}