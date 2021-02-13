package com.czerniecka.customer.service;

import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.dto.CustomerMapper;
import com.czerniecka.customer.entity.Customer;
import com.czerniecka.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public Flux<CustomerDTO> findAll() {

        Flux<Customer> all = customerRepository.findAll();
        return all.map(customerMapper::toCustomerDTO);
    }

    public Mono<CustomerDTO> findCustomerById(String customerId) {

        Mono<Customer> byId = customerRepository.findById(customerId);
        return byId.map(customerMapper::toCustomerDTO);
    }

    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {

        Customer customer = customerMapper.toCustomer(customerDTO);
        Mono<Customer> saved = customerRepository.save(customer);
        return saved.map(customerMapper::toCustomerDTO);
    }

    //TODO
//    public boolean updateCustomer(UUID customerId, CustomerDTO customerDTO) {
//
//        Optional<Customer> c = customerRepository.findById(customerId);
//
//        if(c.isPresent()){
//            Customer customer = c.get();
//            customer.setName(customerDTO.getName());
//            customer.setEmail(customerDTO.getEmail());
//
//            customerRepository.save(customer);
//            return true;
//        }else{
//            return false;
//        }
//    }
}