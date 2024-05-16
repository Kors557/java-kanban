import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import taskmanager.FileBackedTaskManager;
import taskmanager.HistoryManager;
import taskmanager.Managers;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        HistoryManager historyManager = Managers.getDefaultHistory();

        FileBackedTaskManager manager = new FileBackedTaskManager("TestName",historyManager);
        Task task1 = manager.createTask("Test1", "Tecnjdsq nt111", Status.NEW);
        Task task2 = manager.createTask("Test2", "Tecnjdsq nt222", Status.NEW);
        manager.deleteTaskById(task2.getId());

        Epic epic1 = manager.createEpic("EpicTest1", "TestDescription Epic");
        Epic epic2 = manager.createEpic("EpicTest2", "TestDescription Epic");
        manager.deleteEpicsById(epic2.getId());
        SubTask subTask1 = manager.createSubTask("TestSubtask1", "TestDescription1", Status.NEW, epic1.getId());
        SubTask subTask2 = manager.createSubTask("TestSubtask2", "TestDescription2", Status.NEW, epic1.getId());
        manager.deleteSubTaskById(subTask2.getId());

        FileBackedTaskManager manager1 = new FileBackedTaskManager("TestName", historyManager);
        manager1.loadFromFile(new File("TestName"));

        checkLoadFromFile(manager,manager1);



    }

    public static void checkLoadFromFile(FileBackedTaskManager manager, FileBackedTaskManager managerLoad) {
        if (manager.getAllTask().size() == managerLoad.getAllTask().size()) {
            System.out.println("Количество задач совпадает");
        } else {
            System.out.println("Количество задач не совпадает!");
        }

        if (manager.getAllSubTasks().size() == managerLoad.getAllSubTasks().size()) {
            System.out.println("Количество подзадач совпадает");
        } else {
            System.out.println("Количество подзадач не совпадает!");
        }

        if (manager.getAllEpics().size() == managerLoad.getAllEpics().size()) {
            System.out.println("Количество эпиков совпадает");
        } else {
            System.out.println("Количество эпиков не совпадает!");
        }
    }
}