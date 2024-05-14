package taskmanager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String firstString = "id,type,name,status,description,epic";
    private static final File file = new File("TASK_CSV");
    public static HistoryManager historyManager = Managers.getDefaultHistory();

    public FileBackedTaskManager(HistoryManager historyManager) {
        super();
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return super.getAllTask();
    }

    @Override
    public List<Epic> getAllEpicsList() {
        return super.getAllEpicsList();
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return super.getSubTaskById(id);
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task createTask = super.createTask(name, description, status);
        save();
        return createTask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task updateTask(String name, String description, Status status, int id) {
        Task updateTask = super.updateTask(name, description, status, id);
        save();
        return updateTask;
    }

    @Override
    public SubTask createSubTask(String name, String description, Status status, int idEpic) {
        SubTask subTask = super.createSubTask(name, description, status, idEpic);
        save();
        return subTask;
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public SubTask updateSubTask(int id, String name, String description, Status status) {
        SubTask newSubTask = super.updateSubTask(id, name, description, status);
        save();
        return newSubTask;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        save();
        return epic;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpicsById(int id) {
        super.deleteEpicsById(id);
        save();
    }

    @Override
    public Epic updateEpic(int id, String name, String description) {
        Epic newEpic = super.updateEpic(id, name, description);
        save();
        return newEpic;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(firstString + "\n");

            for (Task task : getAllTask()) {
                bufferedWriter.write(toString(task));
            }

            for (Epic epic : getAllEpicsList()) {
                bufferedWriter.write(toString(epic));
            }

            for (SubTask subtask : getAllSubTasks()) {
                bufferedWriter.write(toString(subtask));
            }

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    public String toString(Task task) {
        return switch (task.getType()) {
            case TASK -> task.getId() + ",TASK," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "\n";
            case EPIC -> task.getId() + ",EPIC," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "\n";
            case SUBTASK -> task.getId() + ",SUBTASK," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "," + ((SubTask) task).getIdEpic() + "\n";
        };
    }

    public static Task fromString(String value) { //создание задачи из строки
        if (value == null || value.isBlank()) {
            return null;
        }
        final String[] taskData = value.split(",");
        TaskType type = TaskType.valueOf(taskData[1]);
        switch (type) {
            case TASK:
                Task task = new Task();
                task.setTaskName(taskData[2].trim());
                task.setType(type);
                task.setDescription(taskData[4].trim());
                task.setStatus(Status.valueOf(taskData[3].trim()));
                task.setId(Integer.parseInt(taskData[0].trim()));
                return task;

            case EPIC:
                Epic epic = new Epic();
                epic.setId(Integer.parseInt(taskData[0]));
                epic.setType(type);
                epic.setTaskName(taskData[2]);
                epic.setDescription(taskData[4]);
                epic.setStatus(Status.valueOf(taskData[3]));
                return epic;

            case SUBTASK:
                SubTask subtask = new SubTask();
                subtask.setId(Integer.parseInt(taskData[0]));
                subtask.setType(type);
                subtask.setTaskName(taskData[2]);
                subtask.setDescription(taskData[4]);
                subtask.setStatus(Status.valueOf(taskData[3]));
                subtask.setEpicId(Integer.parseInt(taskData[5]));
                return subtask;
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager);

        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine();
        while (br.ready()) {
            String line = br.readLine();
            String[] splitter = line.split(",");
            TaskType type = TaskType.valueOf(splitter[1]);
            switch (type) {
                case TASK:
                    Task task = fromString(line);
                    allTasks.put(task.getId(), task);
                    break;

                case EPIC:
                    SubTask subTask = (SubTask) fromString(line);
                    int idEpic = subTask.getIdEpic();
                    Epic epic = manager.getEpicById(idEpic);
                    epics.get(epic).add(subTask);
                    break;

                case SUBTASK:
                    SubTask subtaskForSubTask = (SubTask) fromString(line);
                    int idEpicForSubTask = subtaskForSubTask.getIdEpic();
                    Epic epicForSubTask = manager.getEpicById(idEpicForSubTask);
                    ArrayList<SubTask> subTasks = epics.get(epicForSubTask);
                    if (subTasks == null) {
                        subTasks = new ArrayList<>();
                        epics.put(epicForSubTask, subTasks);
                    }
                    subTasks.add(subtaskForSubTask);
            }
        }
        return manager;
    }
}
