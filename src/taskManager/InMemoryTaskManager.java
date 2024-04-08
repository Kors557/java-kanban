package taskManager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager<T extends Task> implements TaskManager {
    private int nextId = 1;
    ArrayList<Task> allTask = new ArrayList<>();

    ArrayList<SubTask> subTasks = new ArrayList<>();

    HashMap<Epic, ArrayList<SubTask>> epics = new HashMap<>();

    private final HistoryManager visitHistory = new InMemoryHistoryManager();


    @Override
    public int generateId() {
        return nextId++;
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task task = new Task(name, description, generateId(), status);
        allTask.add(task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return allTask;
    }

    @Override
    public Task getTaskById(int id) {
        for (Task task : allTask) {
            if (task.getId() == id) {
                visitHistory.add((T) task);
                return task;
            }
        }
        System.out.println("Задачи с данным id не существует");
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        Task removeTask = null;
        for (Task task : allTask) {
            if (task.getId() == id) {
                removeTask = task;
                break;
            }
        }
        allTask.remove(removeTask);
    }

    @Override
    public void deleteAllTasks() {
        allTask.clear();
    }

    @Override
    public Task updateTask(String name, String description, Status status, int id) {
        Task updateTask = null;
        for (Task task : allTask) {
            if (task.getId() == id) {
                deleteTaskById(id);
                updateTask = createTask(name, description, status);
                updateTask.setId(id);
                break;
            }
        }
        return updateTask;
    }

    @Override
    public SubTask createSubTask(String name, String description, Status status, int idEpic) {
        SubTask subTask = new SubTask(name, description, generateId(), status, idEpic);
        subTasks.add(subTask);
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == idEpic) {
                ArrayList<SubTask> epicSubTasks = epics.get(epic);
                if (epicSubTasks == null) {
                    epicSubTasks = new ArrayList<>();
                }
                epicSubTasks.add(subTask);
                epics.put(epic, epicSubTasks);
                checkStatusEpic();
                return subTask;
            }
        }
        System.out.println("Эпика с данным id нет");
        return null;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return subTasks;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == id) {
                visitHistory.add((T) subTask);
                return subTask;
            }
        }
        System.out.println("Подзадачи с данным id не существует");
        return null;
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
    public void deleteById(int id) {
        SubTask removeSubTask = null;
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == id) {
                removeSubTask = subTask;
                break;
            }
        }
        if (removeSubTask != null) {
            subTasks.remove(removeSubTask);
        } else {
            for (Epic epic : epics.keySet()) {
                ArrayList<SubTask> epicSubTasks = epics.get(epic);
                if (epicSubTasks != null) {
                    for (SubTask subTask : epicSubTasks) {
                        if (subTask.getId() == id) {
                            epicSubTasks.remove(subTask);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateSubTask(int id, String name, String description, Status status) {
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == id) {
                subTask.setName(name);
                subTask.setDescription(description);
                subTask.setStatus(status);
                break;
            }
        }

        for (ArrayList<SubTask> epicSubTasks : epics.values()) {
            if (epicSubTasks != null) {
                for (SubTask subTask : epicSubTasks) {
                    if (subTask.getId() == id) {
                        subTask.setName(name);
                        subTask.setDescription(description);
                        subTask.setStatus(status);
                        checkStatusEpic();
                        break;
                    }
                }
            }
        }
    }


    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, generateId());
        epics.put(epic, null);
        return epic;
    }

    @Override
    public HashMap<Epic, ArrayList<SubTask>> getAllEpics() {
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
                subTasks.removeAll(epicSubTasks);
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
