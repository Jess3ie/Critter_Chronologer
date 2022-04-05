package com.udacity.jdnd.course3.critter.controllers;


import com.udacity.jdnd.course3.critter.DTOs.EmployeeDTO;
import com.udacity.jdnd.course3.critter.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    /**
     * Create an employee.
     * Update the employee’s schedule.
     * Save - User controller
     * Find out which employees with the right skills are available on a given date.
     * Schedule one or more employees to do a set of activities with one or more pets.
     */


    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public EmployeeDTO saveEmployee (@RequestBody EmployeeDTO employeeDTO){
        return employeeService.saveEmployee(employeeDTO);
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

}
