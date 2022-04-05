package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.DTOs.CustomerDTO;
import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.exceptions.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.PetNotFoundException;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PetRepository petRepository;

    public CustomerDTO save(CustomerDTO customerDTO) {
        Customer customer = convertCustomerDTOTToCustomer(customerDTO);
        customer = customerRepository.save(customer);
        return convertCustomerToCustomerDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List <CustomerDTO> customerDTOS = new ArrayList<>();
        for (Customer customer : customers) {
            customerDTOS.add(convertCustomerToCustomerDTO(customer));
        }
        return customerDTOS;
    }


    public CustomerDTO getOwnerByPet(long petId) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()){
            Optional <Customer> optionalCustomer = customerRepository.findById(optionalPet.get().getCustomer().getId());
            if (optionalCustomer.isPresent()){
                return convertCustomerToCustomerDTO(optionalCustomer.get());
            } else {
                throw new CustomerNotFoundException("Customer for pet id= " + petId + " was not found");
            }
        } else {
            throw new PetNotFoundException("Pet with id= " + petId + " not found");
        }
    }

    //convert entity to DTO
    private CustomerDTO convertCustomerToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        //if there already is a pet to that customer, then need to create a list of pets for that customer
        if (customer.getPets() != null) {
            List <Long> petIds = new ArrayList<>();
            customer.getPets().forEach(pet -> petIds.add(pet.getId()));
            customerDTO.setPetIds(petIds);
        }
        return customerDTO;
    }

    //convert DTO to entity
    private Customer convertCustomerDTOTToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        if (customer.getPets() != null){
            List <Pet> pets = new ArrayList<>();
            customerDTO.getPetIds().forEach(petId -> pets.add(petRepository.findById(petId).get()));
            customer.setPets(pets);
        }

        return customer;
    }

}
