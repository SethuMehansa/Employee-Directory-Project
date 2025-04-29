package com.fortium.edp.controller;

import com.fortium.edp.dto.Employee;
import com.fortium.edp.service.EmployeeService;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/email")
    public ResponseEntity<Employee> getEmployeeByEmail(@RequestParam String email) {
        Employee employee = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Validated @RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Validated @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {

        response.setHeader("Content-Disposition", "attachment; filename=\"employees.csv\"");
        response.setContentType("text/csv");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{
                    "ID", "Name", "Email", "Department", "Created At", "Updated At"
            });

            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            employeeService.getAllEmployees().forEach(dto ->
                    writer.writeNext(new String[]{
                            dto.getId() != null ? dto.getId().toString() : "",
                            dto.getName(),
                            dto.getEmail(),
                            dto.getDepartment(),
                            dto.getCreatedAt() != null ? fmt.format(dto.getCreatedAt()) : "",
                            dto.getUpdatedAt() != null ? fmt.format(dto.getUpdatedAt()) : ""
                    })
            );

        }
    }

}
