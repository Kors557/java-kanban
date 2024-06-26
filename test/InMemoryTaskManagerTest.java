package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import taskmanager.HistoryManager;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private HistoryManager visitHistory;

    @BeforeEach
    void setUp() {
        visitHistory = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(visitHistory);
    }


    @Test
    void testCreateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(visitHistory);
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        assertEquals(1, task.getId());
        assertEquals("Task 1", task.getName());
        assertEquals("Description 1", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void testGetTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(visitHistory);
        Task task = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testCreateAndDeleteTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager(visitHistory);
        Task task = manager.createTask("Test task", "Description", Status.NEW);
        assertEquals(1, task.getId());
        assertEquals(1, manager.getAllTask().size());

        manager.deleteTaskById(1);
        assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    public void testCreateAndUpdateTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager(visitHistory);
        Task task = manager.createTask("Test task", "Description", Status.NEW);
        Task updatedTask = manager.updateTask("Updated task", "Updated description", Status.IN_PROGRESS, 1);
        assertEquals("Updated task", updatedTask.getName());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    void createTask() {
        Task task = taskManager.createTask("New Task", "Description", Status.NEW);
        assertNotNull(task);
        assertEquals("New Task", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void getTaskById() {
        Task task1 = taskManager.createTask("New Task 1", "Description 1", Status.NEW);
        Task task2 = taskManager.createTask("New Task 2", "Description 2", Status.IN_PROGRESS);

        Task foundTask = taskManager.getTaskById(task1.getId());
        assertNotNull(foundTask);
        assertEquals("New Task 1", foundTask.getName());
        assertEquals("Description 1", foundTask.getDescription());
        assertEquals(Status.NEW, foundTask.getStatus());
    }

    @Test
    void deleteTaskById() {
        Task task = taskManager.createTask("New Task", "Description", Status.NEW);
        assertNotNull(task);
        int taskId = task.getId();
        taskManager.deleteTaskById(taskId);
        assertNull(taskManager.getTaskById(taskId));
    }
}
