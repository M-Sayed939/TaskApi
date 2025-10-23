package com.example.TaskApi.services;

import com.example.TaskApi.dto.TaskRequest;
import com.example.TaskApi.exception.TaskNotFoundException;
import com.example.TaskApi.model.Task;
import com.example.TaskApi.model.TaskStatus;
import com.example.TaskApi.model.User;
import com.example.TaskApi.repository.TaskRepository;
import com.example.TaskApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;



    public Task createTask(TaskRequest taskRequest, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(TaskStatus.OPEN)
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(String userEmail) {
        User user = getUserByEmail(userEmail);
        return taskRepository.findByUserId(user.getId());
    }

    public Task updateTaskStatus(Long taskId, TaskStatus status, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = getTaskById(taskId);

        if (!task.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this task");
        }
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Task task = getTaskById(taskId);
        if (!task.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }
}
