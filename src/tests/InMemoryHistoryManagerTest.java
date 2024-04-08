package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import taskManager.InMemoryHistoryManager;
import taskManager.InMemoryTaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
        task = taskManager.createTask("test", "testing", Status.NEW);
    }


    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {

        for (int i = 1; i <= 10; i++) {
            historyManager.add(taskManager.createTask("Task " + i, "Description " + i, Status.DONE));
        }
        historyManager.add(taskManager.createTask("New Task", "New Description", Status.IN_PROGRESS));
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertFalse(history.contains(taskManager.createTask("Task 1", "Description 1", Status.DONE)));
    }

    @Test
    void getHistory_ShouldReturnEmptyList_WhenNoTasksAdded() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }
}
