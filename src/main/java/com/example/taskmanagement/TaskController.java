
package com.example.taskmanagement;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = {
    "http://localhost:5174",
    "http://localhost:8080",
    "http://task-management-frontend-satish-2026.s3-website.ap-south-1.amazonaws.com",
    "http://16.4.43.76"
})
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return repository.save(task);
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody Task task) {

        Task existing =
                repository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setStatus(task.getStatus());
        existing.setPriority(task.getPriority());

        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public Task deleteTask(@PathVariable Long id) {

        Task existing =
                repository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setStatus("DELETED");

        return repository.save(existing);
    }
}

