import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int nextId = 1;
    ArrayList<Task> allTask = new ArrayList<>();

    ArrayList<SubTask> subTasks = new ArrayList<>();

    HashMap<Epic, ArrayList<SubTask>> epics = new HashMap<>();

    public int generateId() {
        return nextId++;
    }

    public Task createTask(String name, String description, Status status) {
        Task task = new Task(name, description, generateId(), status);
        allTask.add(task);
        return task;
    }

    public ArrayList<Task> getAllTask() {
        return allTask;
    }

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

    public void deleteAllTasks() {
        allTask.clear();
    }

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

    public SubTask createSubTask(String name, String description, Status status, int idEpic) {
        SubTask subTask = new SubTask(name, description, generateId(), status);
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

    public ArrayList<SubTask> getAllSubTasks() {
        return subTasks;
    }

    public SubTask getSubTaskById(int id) {
        for (SubTask subTask : subTasks) {
            if (subTask.getId() == id) {
                return subTask;
            }
        }
        System.out.println("Подзадачи с данным id не существует");
        return null;
    }

    public void deleteAllSubTasks() {
        subTasks.clear();

        for (ArrayList<SubTask> epicSubTasks : epics.values()) {
            if (epicSubTasks != null) {
                epicSubTasks.clear();
            }
        }
    }

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


    public Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description, generateId());
        epics.put(epic, null);
        return epic;
    }

    public HashMap<Epic, ArrayList<SubTask>> getAllEpics() {
        return epics;
    }

    public Epic getEpicById(int id) {
        for (Epic epic : epics.keySet()) {
            if (epic.getId() == id) {
                return epic;
            }
        }
        System.out.println("Эпик с указанным id не найден");
        return null;
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

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
                return epics.get(epic);
            }
        }
        System.out.println("Эпик с указанным id не найден");
        return new ArrayList<>();
    }

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

}
