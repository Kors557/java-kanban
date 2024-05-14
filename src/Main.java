import task.Status;
import taskmanager.FileBackedTaskManager;
import taskmanager.HistoryManager;
import taskmanager.Managers;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        HistoryManager historyManager = Managers.getDefaultHistory();

        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager);
        manager.createTask("Test", "Tecnjdsq nt", Status.NEW);
        manager.createEpic("EpicTest", "TestDescription Epic");


    }
}