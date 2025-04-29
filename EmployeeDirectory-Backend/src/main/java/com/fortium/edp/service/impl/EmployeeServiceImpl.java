package com.fortium.edp.service.impl;

import com.fortium.edp.dto.Employee;
import com.fortium.edp.entity.EmployeeEntity;
import com.fortium.edp.repository.EmployeeRepository;
import com.fortium.edp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final String EMPLOYEE_NOT_FOUND_BY_ID = "Employee not found with id ";
    private static final String EMPLOYEE_NOT_FOUND_BY_EMAIL = "Employee not found with email ";
    private static final String EMAIL_ALREADY_EXISTS = "Email already exists";

    private final EmployeeRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        for (EmployeeEntity entity : repository.findAll()) {
            Employee map = modelMapper.map(entity, Employee.class);
            list.add(map);
        }
        return list;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        EmployeeEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND_BY_ID + id));
        return modelMapper.map(entity, Employee.class);
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        EmployeeEntity entity = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND_BY_EMAIL + email));
        return modelMapper.map(entity, Employee.class);
    }

    @Override
    public Employee createEmployee(Employee dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXISTS);
        }
        EmployeeEntity entity = modelMapper.map(dto, EmployeeEntity.class);
        EmployeeEntity saved = repository.save(entity);
        return modelMapper.map(saved, Employee.class);
    }

    @Override
    public Employee updateEmployee(Long id, Employee dto) {
        EmployeeEntity existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(EMPLOYEE_NOT_FOUND_BY_ID + id));

        if (!existing.getEmail().equals(dto.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXISTS);
        }

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setDepartment(dto.getDepartment());
        EmployeeEntity updated = repository.save(existing);
        return modelMapper.map(updated, Employee.class);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(EMPLOYEE_NOT_FOUND_BY_ID + id);
        }
        repository.deleteById(id);
    }
}
