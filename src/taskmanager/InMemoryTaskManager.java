package taskmanager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    static Map<Integer, Task> allTasks = new HashMap<>();

    static Map<Integer, SubTask> subTasks = new HashMap<>();

    static Map<Epic, ArrayList<SubTask>> epics = new HashMap<>();

    private final HistoryManager visitHistory;

    public InMemoryTaskManager(HistoryManager visitHistory) {
        this.visitHistory = visitHistory;
    }


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
        if (allTasks.get(id) != null) {
            visitHistory.add(allTasks.get(id));
            return allTasks.get(id);
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        allTasks.remove(id);
        visitHistory.remove(id);
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

        ArrayList<SubTask> newList = epics.get(getEpicById(idEpic));
        if (newList == null) {
            newList = new ArrayList<>();
            epics.put(getEpicById(idEpic), newList);
        }

        newList.add(subTask);
        checkStatusEpic();
        epics.put(getEpicById(idEpic), newList);
        return subTask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public SubTask getSubTaskById(int id) {
        visitHistory.add(subTasks.get(id));
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
            ArrayList<SubTask> listSubtask = epics.get(getEpicById(epicId));
            if(listSubtask != null) {
                listSubtask.remove(getSubTaskById(id));
                epics.put(getEpicById(epicId), listSubtask);
            }
            subTasks.remove(id);
            visitHistory.remove(id);
        }
    }

    @Override
    public SubTask updateSubTask(int id, String name, String description, Status status) {
        int idEpic = subTasks.get(id).getIdEpic();
        SubTask newSubTask = createSubTask(name, description, status, idEpic);
        newSubTask.setId(id);
        subTasks.put(newSubTask.getId(), newSubTask);
        epics.get(idEpic).remove(id);
        epics.get(idEpic).add(newSubTask);
        checkStatusEpic();
        return newSubTask;
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
    public List<Epic> getAllEpicsList() {
        return new ArrayList<Epic>(epics.keySet());
    }

    @Override
    public Epic getEpicById(int id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                visitHistory.add(epic);
                return epic;
            }
        }
        System.out.println("Эпик с указанным id не найден 3");
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
                for (SubTask subTask : epicSubTasks) {
                    subTasks.remove(subTask.getId());
                }
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


    public ArrayList<SubTask> getSubTasksByIdEpic(int id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                visitHistory.add(epics.get(epic).get(id));
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
