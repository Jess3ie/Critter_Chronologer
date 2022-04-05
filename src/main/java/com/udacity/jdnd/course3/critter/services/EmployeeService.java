package com.udacity.jdnd.course3.critter.services;


import com.udacity.jdnd.course3.critter.DTOs.EmployeeDTO;
import com.udacity.jdnd.course3.critter.DTOs.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.exceptions.EmployeeNotFoundException;
import com.udacity.jdnd.course3.critter.repositories.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class EmployeeService {

    /**
     * Create an employee.
     * Update the employee’s schedule.
     * Save - User controller
     * Find out which employees with the right skills are available on a given date.
     * Schedule one or more employees to do a set of activities with one or more pets.
     */

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertEmployeeDTOToEmployee(employeeDTO);
        employee= employeeRepository.save(employee);
        return convertEmployeeToEmployeeDTO(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        for (Employee employee : employees) {
            employeeDTOS.add(convertEmployeeToEmployeeDTO(employee));
        }
        return employeeDTOS;
    }

    public EmployeeDTO getEmployeeById(long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            return convertEmployeeToEmployeeDTO(optionalEmployee.get());
        } else {
            throw new EmployeeNotFoundException("Employee with id= " + employeeId + " not found.");
        }
    }

    public void setEmployeeAvailability(long employeeId, Set<DayOfWeek> daysAvailable) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setDaysAvailable(daysAvailable);
            employeeRepository.save(employee);
        } else throw new EmployeeNotFoundException("Employee with id= " + employeeId + " not found.");
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeRequestDTO) {
        Set<EmployeeSkill> employeeSkills = employeeRequestDTO.getSkills();
        DayOfWeek daysAvailable = employeeRequestDTO.getDate().getDayOfWeek();
        List <Employee> employees = employeeRepository.findAllByDaysAvailable(daysAvailable);
        List <EmployeeDTO> employeeDTOS = new ArrayList<>();
        for (Employee employee : employees) {
            // check if employee has skills
            if (employee.getSkills().containsAll(employeeSkills)){
                employeeDTOS.add(convertEmployeeToEmployeeDTO(employee));
            }
        }

        return employeeDTOS;
    }

    //Convert DTO to entity
    private Employee convertEmployeeDTOToEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    //Convert entity to DTO
    private EmployeeDTO convertEmployeeToEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

}
