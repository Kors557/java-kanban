package taskmanager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String FIRST_STRING = "id,type,name,status,description,epic";
    private final File file;
    public static HistoryManager historyManager = Managers.getDefaultHistory();

    public FileBackedTaskManager(String fileName, HistoryManager historyManager) {
        super(historyManager);
        this.file = new File(fileName);
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task createTask = super.createTask(name, description, status);
        save();
        return createTask;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
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
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public SubTask createSubTask(String name, String description, Status status, int idEpic) {
        SubTask subTask = super.createSubTask(name, description, status, idEpic);
        save();
        return subTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
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
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public Epic createEpic(String name, String description, Instant startTime, long duration) {
        Epic epic = super.createEpic(name, description, startTime, duration);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
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

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(FIRST_STRING + "\n");

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

    private String toString(Task task) {
        return switch (task.getType()) {
            case TASK -> task.getId() + ",TASK," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "\n";
            case EPIC -> task.getId() + ",EPIC," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "\n";
            case SUBTASK -> task.getId() + ",SUBTASK," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "," + ((SubTask) task).getIdEpic() + "\n";
        };
    }

    private static Task fromString(String value) {
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

    public void loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getName(), historyManager);

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
                    Epic epic = (Epic) fromString(line);
                    ArrayList<SubTask> subTaskForEpic = new ArrayList<>();
                    if (epics.get(epic) != null) {
                        subTaskForEpic = epics.get(epic);
                    }
                    epics.put(epic, subTaskForEpic);
                    break;

                case SUBTASK:
                    SubTask subtask = (SubTask) fromString(line);
                    int idEpic1 = subtask.getIdEpic();
                    Epic epic2 = getEpicById(idEpic1);
                    ArrayList<SubTask> newSubTasks;
                    if (!epics.containsKey(epic2)) {
                        newSubTasks = new ArrayList<>();
                        epics.put(epic2, newSubTasks);
                    } else {
                        newSubTasks = epics.get(epic2);
                    }
                    subTasks.put(subtask.getId(), subtask);
                    break;

            }
        }
    }
}
