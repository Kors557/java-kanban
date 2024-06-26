package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;
import taskmanager.HistoryManager;
import taskmanager.InMemoryHistoryManager;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager visitHistory = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(visitHistory);
        historyManager = new InMemoryHistoryManager();
        task = taskManager.createTask("test", "testing", Status.NEW);
    }

    @Test
    void add() {
        // Добавление задачи в историю и проверка, что она там есть
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "Размер истории должен быть 1 после добавления задачи.");
        assertEquals(task, history.get(0), "Добавленная задача должна быть в истории.");
    }

    @Test
    void getHistory() {
        // Добавление нескольких задач в историю и проверка размера и содержимого
        for (int i = 1; i <= 10; i++) {
            historyManager.add(taskManager.createTask("Task " + i, "Description " + i, Status.DONE));
        }
        historyManager.add(taskManager.createTask("New Task", "New Description", Status.IN_PROGRESS));
        List<Task> history = historyManager.getHistory();
        assertEquals(11, history.size(), "Размер истории должен быть 11 после добавления 11 задач.");
        assertFalse(history.contains(taskManager.createTask("Task 1", "Description 1", Status.DONE)), "История не должна содержать задачу, которая не была добавлена.");
    }

    @Test
    void getHistoryShouldReturnEmptyListWhenNoTasksAdded() {
        // Получение истории, когда задачи не были добавлены
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть пустой.");
        assertTrue(history.isEmpty(), "История должна быть пустой, когда задачи не были добавлены.");
    }
}
