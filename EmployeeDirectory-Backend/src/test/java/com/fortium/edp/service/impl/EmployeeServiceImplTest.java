package com.fortium.edp.service.impl;

import com.fortium.edp.dto.Employee;
import com.fortium.edp.entity.EmployeeEntity;
import com.fortium.edp.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl service;

    private EmployeeEntity employeeEntity;
    private Employee employeeDTO;

    @BeforeEach
    void setUp() {
        employeeEntity = EmployeeEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .build();

        employeeDTO = Employee.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .build();
    }

    @Test
    @DisplayName("Test getAllEmployees()")
    void testGetAllEmployees() {
        when(repository.findAll()).thenReturn(Collections.singletonList(employeeEntity));
        when(modelMapper.map(employeeEntity, Employee.class)).thenReturn(employeeDTO);

        List<Employee> result = service.getAllEmployees();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test getEmployeeById() - Found")
    void testGetEmployeeByIdFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(employeeEntity));
        when(modelMapper.map(employeeEntity, Employee.class)).thenReturn(employeeDTO);

        Employee result = service.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Test getEmployeeById() - Not Found")
    void testGetEmployeeByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEmployeeById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found with id 1");
    }

    @Test
    @DisplayName("Test getEmployeeByEmail() - Found")
    void testGetEmployeeByEmailFound() {
        when(repository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(employeeEntity));
        when(modelMapper.map(employeeEntity, Employee.class)).thenReturn(employeeDTO);

        Employee result = service.getEmployeeByEmail("john.doe@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Test getEmployeeByEmail() - Not Found")
    void testGetEmployeeByEmailNotFound() {
        when(repository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEmployeeByEmail("john.doe@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found with email john.doe@example.com");
    }

    @Test
    @DisplayName("Test createEmployee() - Success")
    void testCreateEmployeeSuccess() {
        when(repository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(modelMapper.map(employeeDTO, EmployeeEntity.class)).thenReturn(employeeEntity);
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(modelMapper.map(employeeEntity, Employee.class)).thenReturn(employeeDTO);

        Employee result = service.createEmployee(employeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(any(EmployeeEntity.class));
    }

    @Test
    @DisplayName("Test createEmployee() - Email Exists")
    void testCreateEmployeeEmailExists() {
        when(repository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createEmployee(employeeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Test updateEmployee() - Success")
    void testUpdateEmployeeSuccess() {
        when(repository.findById(1L)).thenReturn(Optional.of(employeeEntity));

        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(modelMapper.map(employeeEntity, Employee.class)).thenReturn(employeeDTO);

        Employee result = service.updateEmployee(1L, employeeDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(repository, times(1)).save(any(EmployeeEntity.class));
    }

    @Test
    @DisplayName("Test updateEmployee() - Not Found")
    void testUpdateEmployeeNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateEmployee(1L, employeeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found with id 1");
    }

    @Test
    @DisplayName("Test updateEmployee() - Email Exists")
    void testUpdateEmployeeEmailExists() {
        EmployeeEntity existingEntity = EmployeeEntity.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .department("HR")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(repository.existsByEmail("john.doe@example.com")).thenReturn(true);

        employeeDTO.setEmail("john.doe@example.com");

        assertThatThrownBy(() -> service.updateEmployee(1L, employeeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Test deleteEmployee() - Success")
    void testDeleteEmployeeSuccess() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.deleteEmployee(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test deleteEmployee() - Not Found")
    void testDeleteEmployeeNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteEmployee(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employee not found with id 1");
    }
}
