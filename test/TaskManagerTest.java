package test;

import exception.ManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import taskmanager.HistoryManager;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest <T extends TaskManager>{

    protected T manager;

    protected HistoryManager visitHistory;

    protected Task createTask() {
        return new Task("Name", "Description", 0, Status.NEW, Instant.now(), 0);
    }

    protected Epic createEpic() {
        return new Epic("Name", "Description", 1, Instant.now(), 0);
    }

    protected SubTask createSubtask(Epic epic) {
        return new SubTask("Name", "Description", 2, Status.NEW, epic.getId(), Instant.now(), 0);
    }

    @BeforeEach
    public void setUp() {
        visitHistory = Managers.getDefaultHistory();
        manager = (T) new InMemoryTaskManager(visitHistory);
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasks = manager.getAllTask();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Epic> epics = manager.getAllEpicsList();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubtask(epic);
        manager.createSubTask(subtask);
        List<SubTask> subtasks = manager.getAllSubTasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getIdEpic());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldRemoveAllTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteAllTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
    }

    @Test
    public void shouldRemoveAllEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpicsList());
    }

    @Test
    public void shouldRemoveTaskById() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
    }

    @Test
    public void shouldNotDeleteTaskIfBadId() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(228);
        assertEquals(List.of(task), manager.getAllTask());
    }


    @Test
    public void shouldDoNothingIfTaskHashMapIsEmpty() {
        manager.deleteAllTasks();
        manager.deleteTaskById(999);
        assertEquals(0, manager.getAllTask().size());
    }

    @Test
    public void shouldDoNothingIfEpicHashMapIsEmpty() {
        manager.deleteAllEpics();
        manager.deleteTaskById(999);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldDoNothingIfSubtaskHashMapIsEmpty() {
        manager.deleteAllEpics();
        manager.deleteTaskById(999);
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    void testUpdateTimeEpic() {

        Epic epic = new Epic("Epic", "Description", 0);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Subtask 1", "Description 1",0, Status.NEW, epic.getId(), Instant.parse("2023-10-26T10:00:00Z"), (60));
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Subtask 2", "Description 2",0, Status.NEW, epic.getId(), Instant.parse("2022-10-26T12:00:00Z"), (30));

        manager.createSubTask(subTask2);

        manager.updateTimeEpic(epic);


        assertEquals(Instant.parse("2022-10-26T12:00:00Z"), epic.getStartTime());
        assertEquals(Instant.parse("2023-10-26T11:00:00Z"), epic.getEndTime());
    }
}


