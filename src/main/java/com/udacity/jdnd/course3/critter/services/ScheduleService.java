package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.DTOs.ScheduleDTO;
import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.exceptions.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.EmployeeNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.PetNotFoundException;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import com.udacity.jdnd.course3.critter.repositories.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import com.udacity.jdnd.course3.critter.repositories.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ScheduleDTO save(ScheduleDTO scheduleDTO, List<Long> employeeIds, List<Long> petIds) {
        Schedule schedule = convertScheduleDTOToSchedule(scheduleDTO);

        List<Employee> employees = employeeRepository.findAllById(employeeIds);
        List<Pet> pets = petRepository.findAllById(petIds);
        schedule.setEmployeeIds(employees);
        schedule.setPetIds(pets);
        schedule.setDate(scheduleDTO.getDate());
        schedule.setActivities(scheduleDTO.getActivities());
        return convertScheduleToScheduleDTO(scheduleRepository.save(schedule));
    }


    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (Schedule schedule : schedules) {
            scheduleDTOS.add(convertScheduleToScheduleDTO(schedule));
        }
        return scheduleDTOS;
    }

    public List<ScheduleDTO> getScheduleForPetByPetId(long petId) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()) {
            List<Schedule> schedules = scheduleRepository.findAllByPetIds(optionalPet.get());
            List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
            for (Schedule schedule : schedules) {
                scheduleDTOS.add(convertScheduleToScheduleDTO(schedule));
            }
            return scheduleDTOS;
        } else {
            throw new PetNotFoundException("Pet with id= " + petId + " not found");
        }
    }

    public List<ScheduleDTO> getScheduleForEmployeeByEmployeeId(long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()){
            List<Schedule> schedules = scheduleRepository.findAllByEmployeeIds(optionalEmployee.get());
            List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
            for (Schedule schedule : schedules) {
                scheduleDTOS.add(convertScheduleToScheduleDTO(schedule));
            }
            return scheduleDTOS;
        }
        else {
            throw new EmployeeNotFoundException("Employee with id= " + employeeId + " not found");
        }
    }


    public List<ScheduleDTO> getScheduleForCustomerByCustomerId(long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            List<Pet> pets = optionalCustomer.get().getPets();
            List<Schedule> schedules = scheduleRepository.findAllByPetIdsIn(pets);
            List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
            for (Schedule schedule : schedules) {
                scheduleDTOS.add(convertScheduleToScheduleDTO(schedule));
            }
            return scheduleDTOS;
        }
        else {
            throw new CustomerNotFoundException("Customer with id= " + customerId + " not found");
        }
    }

    //convert DTO to entity
    private Schedule convertScheduleDTOToSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        return schedule;
    }

    //convert entity to DTO
    private ScheduleDTO convertScheduleToScheduleDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);
        //mapping variable that have different variable types

        scheduleDTO.setEmployeeIds(schedule.getEmployeeIds().stream().map(Employee::getId).collect(Collectors.toList()));
        scheduleDTO.setPetIds(schedule.getPetIds().stream().map(Pet::getId).collect(Collectors.toList()));
        return scheduleDTO;

    }
}
