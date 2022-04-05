package com.udacity.jdnd.course3.critter.services;


import com.udacity.jdnd.course3.critter.DTOs.PetDTO;
import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.exceptions.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.PetNotFoundException;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public PetDTO save(PetDTO petDTO) {
        Pet pet = convertPetDTOToPet(petDTO);
        pet = petRepository.save(pet);
        if (pet.getCustomer() != null) {
            pet.getCustomer().addPet(pet);
            customerRepository.save(pet.getCustomer());
        }
        return convertPetToPetDTO(pet); // returns petDTO
    }

    public PetDTO getPetById(long petId) {
        //Optional:  may or may not be returned depending on whether it exists
        Optional <Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()) {
            return convertPetToPetDTO(optionalPet.get());
        } else {
            throw new PetNotFoundException("Pet with id= " + petId + " not found");
        }
    }

    public List<PetDTO> getPets() {
        List<Pet> pets = petRepository.findAll();
        List<PetDTO> petDTOS = new ArrayList<>();
        for (Pet pet : pets) {
            petDTOS.add(convertPetToPetDTO(pet));
        }
        return petDTOS;
    }

    public List<PetDTO> getPetsByOwnerId(long ownerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(ownerId);
        if (optionalCustomer.isPresent()) {
            List <Pet> pets = petRepository.findAllByCustomerId(ownerId);
            List <PetDTO> petDTOS = new ArrayList<>();
            for (Pet pet : pets) {
                petDTOS.add(convertPetToPetDTO(pet));
            }
            return petDTOS;
        } else {
            throw new CustomerNotFoundException("Customer with id= " + ownerId + " not found.");
        }
    }

    //Convert DTO to entity so return entity
    private Pet convertPetDTOToPet (PetDTO petDTO) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet);
        if (petDTO.getOwnerId() != 0) {
            pet.setCustomer(customerRepository.findById(petDTO.getOwnerId()).get());
        }

        return pet;
    }

    //Convert entity to DTO so return DTO
    private PetDTO convertPetToPetDTO (Pet pet) {
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(pet, petDTO);
        if (pet.getCustomer() != null) {
            petDTO.setOwnerId(pet.getCustomer().getId());
        }

        return petDTO;
    }
}
