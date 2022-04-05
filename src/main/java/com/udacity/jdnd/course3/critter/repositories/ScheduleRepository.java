package com.udacity.jdnd.course3.critter.repositories;


import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByPetIdsIn(List<Pet> pet);
    List<Schedule> findAllByEmployeeIds(Employee employee);
    List<Schedule> findAllByPetIds(Pet pet);
}
