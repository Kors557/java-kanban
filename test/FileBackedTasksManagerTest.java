package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Task;
import taskmanager.FileBackedTaskManager;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    public static final Path path = Path.of("data.test.csv");
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file.toString(), Managers.getDefaultHistory());
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task("Description", "Title",0, Status.NEW, Instant.now(), 0);
        manager.createTask(task);
        Epic epic = new Epic("Description", "Title", 0, Instant.now(), 0);
        manager.createEpic(epic);
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toString(), Managers.getDefaultHistory());
        assertEquals(List.of(task), manager.getAllTask());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toString(), Managers.getDefaultHistory());
        fileManager.save();
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toString(), Managers.getDefaultHistory());
        fileManager.save();
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}
