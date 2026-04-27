package com.student.employees;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository repo;

    /**
     * Constructor: si la tabla está vacía, inserta 3 empleados de ejemplo.
     * Se usa {@link TransactionTemplate} para que el seeding ocurra dentro de una transacción
     * (JPA requiere transacción al guardar; el constructor solo no recibe proxies @Transactional).
     */
    public EmployeeController(EmployeeRepository repo, TransactionTemplate transactionTemplate) {
        this.repo = repo;
        transactionTemplate.executeWithoutResult(
                status -> {
                    if (repo.count() == 0) {
                        repo.saveAll(
                                List.of(
                                        new Employee(null, "Ana García", "IT", new BigDecimal("52000.00")),
                                        new Employee(null, "Luis Pérez", "RRHH", new BigDecimal("48000.00")),
                                        new Employee(null, "Marta López", "IT", new BigDecimal("55000.00"))));
                    }
                });
    }

    @GetMapping
    public List<Employee> list() {
        return repo.findAll();
    }

    /** Ruta literal antes de /{id} para que no se interprete "by-department" como id. */
    @GetMapping("/by-department")
    public List<Employee> byDepartment(@RequestParam String department) {
        return repo.findByDepartment(department);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee create(@RequestBody Employee body) {
        body.setId(null);
        return repo.save(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
