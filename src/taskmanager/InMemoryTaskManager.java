package taskmanager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager<T extends Task> implements TaskManager {
    private int nextId = 1;
    Map<Integer, Task> allTasks = new HashMap<>();

    Map<Integer, SubTask> subTasks = new HashMap<>();

    Map<Epic, ArrayList<SubTask>> epics = new HashMap<>();

    private final HistoryManager visitHistory = new InMemoryHistoryManager();


    @Override
    public int generateId() {
        return nextId++;
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task task = new Task(name, description, generateId(), status);
        allTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        ArrayList<Task> list = new ArrayList<>();
        list.addAll(allTasks.values());
        return list;
    }

    @Override
    public Task getTaskById(int id) {
        visitHistory.add(allTasks.get(id));
        return allTasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        if (allTasks.containsKey(id)) {
            allTasks.remove(id);
        } else {
            return;
        }
    }

    @Override
    public void deleteAllTasks() {
        allTasks.clear();
    }

    @Override
    public Task updateTask(String name, String description, Status status, int id) {
        Task updateTask = createTask(name, description, status);
        updateTask.setId(id);
        allTasks.put(updateTask.getId(), updateTask);
        return updateTask;
    }

    @Override
    public SubTask createSubTask(String name, String description, Status status, int idEpic) {
        SubTask subTask = new SubTask(name, description, generateId(), status, idEpic);
        subTasks.put(subTask.getId(), subTask);
        getSubTasksByIdEpic(idEpic).add(subTask);
        checkStatusEpic();
        return subTask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        subTaskArrayList.addAll(subTasks.values());
        return subTaskArrayList;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (ArrayList<SubTask> epicSubTasks : epics.values()) {
            if (epicSubTasks != null) {
                epicSubTasks.clear();
            }
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getIdEpic();
            subTasks.remove(id);
            epics.get(epicId).remove(id);
        }
    }

    @Override
    public void updateSubTask(int id, String name, String description, Status status) {
        int idEpic = subTasks.get(id).getIdEpic();
        SubTask newSubTask = createSubTask(name, description, status, idEpic);
        newSubTask.setId(id);
        subTasks.put(newSubTask.getId(), newSubTask);
        epics.get(idEpic).remove(id);
        epics.get(idEpic).add(newSubTask);
        checkStatusEpic();
    }


    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, generateId());
        epics.put(epic, null);
        return epic;
    }

    @Override
    public Map<Epic, ArrayList<SubTask>> getAllEpics() {
        return epics;
    }

    @Override
    public Epic getEpicById(int id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                visitHistory.add((T) epic);
                return epic;
            }
        }
        System.out.println("Эпик с указанным id не найден");
        return null;
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteEpicsById(int id) {
        Epic epicToRemove = null;
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                epicToRemove = epic;
                break;
            }
        }

        if (epicToRemove != null) {
            epics.remove(epicToRemove);
            ArrayList<SubTask> epicSubTasks = epics.get(epicToRemove);
            if (epicSubTasks != null) {
                subTasks.remove(epicSubTasks);
            }
        } else {
            System.out.println("Эпик с указанным id не найден");
        }
    }

    @Override
    public Epic updateEpic(int id, String name, String description) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                epic.setName(name);
                epic.setDescription(description);
                return epic;
            }
        }
        System.out.println("Эпик с указанным id не найден");
        return null;
    }

    @Override
    public ArrayList<SubTask> getSubTasksByIdEpic(int id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                return epics.get(epic);
            }
        }
        System.out.println("Эпик с указанным id не найден");
        return new ArrayList<>();
    }

    @Override
    public void checkStatusEpic() {
        for (Epic epic : epics.keySet()) {
            ArrayList<SubTask> epicSubTasks = epics.get(epic);
            if (epicSubTasks == null || epicSubTasks.stream().allMatch(subTask -> subTask.getStatus() == Status.NEW)) {
                epic.setStatus(Status.NEW);
            } else if (epicSubTasks.stream().allMatch(subTask -> subTask.getStatus() == Status.DONE)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public List<Task> getHistory() {
        return visitHistory.getHistory();
    }
}
