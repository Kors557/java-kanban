package taskmanager;

import exception.ManagerException;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    static Map<Integer, Task> allTasks = new HashMap<>();

    static Map<Integer, SubTask> subTasks = new HashMap<>();

    static Map<Epic, ArrayList<SubTask>> epics = new HashMap<>();

    private static HistoryManager visitHistory = null;

    public InMemoryTaskManager(HistoryManager visitHistory) {
        InMemoryTaskManager.visitHistory = visitHistory;
    }

    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);


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
    public Task createTask(Task task) {
        int newId = generateId();
        task.setId(newId);
        addNewPrioritizedTask(task);
        allTasks.put(newId, task);
        return task;
    }


    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(allTasks.values());
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
    public Task updateTask(Task task) {
        if (task != null && allTasks.containsKey(task.getId())) {
            addNewPrioritizedTask(task);
            allTasks.put(task.getId(), task);
        } else {
            System.out.println("Task не найдена");
        }
        return task;
    }

    @Override
    public SubTask createSubTask(String name, String description, Status status, int idEpic) {
        SubTask subTask = new SubTask(name, description, generateId(), status, idEpic);
        subTasks.put(subTask.getId(), subTask);

        ArrayList<SubTask> newList = epics.computeIfAbsent(getEpicById(idEpic), k -> new ArrayList<>());

        newList.add(subTask);
        checkStatusEpic();
        epics.put(getEpicById(idEpic), newList);
        return subTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        int newId = generateId();
        subTask.setId(newId);
        Epic epic = getEpicById(subTask.getIdEpic());
        if (epic != null) {
            addNewPrioritizedTask(subTask);
            subTasks.put(newId, subTask);
            ArrayList<SubTask> newList = epics.computeIfAbsent(getEpicById(subTask.getIdEpic()), k -> new ArrayList<>());
            newList.add(subTask);
            epics.put(getEpicById(subTask.getIdEpic()), newList);
            checkStatusEpic();
            updateTimeEpic(epic);
            return subTask;
        } else {
            System.out.println("Epic не найден при выполнении createSubTask");
            return null;
        }
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
            if (listSubtask != null) {
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
        epics.get(getEpicById(idEpic)).remove(id);
        epics.get(getEpicById(idEpic)).add(newSubTask);
        checkStatusEpic();
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subtask != null && subTasks.containsKey(subtask.getId())) {
            addNewPrioritizedTask(subtask);
            int idEpic = subTasks.get(subtask.getId()).getIdEpic();
            subTasks.put(subtask.getId(), subtask);
            epics.get(getEpicById(idEpic)).remove(subtask.getId());
            epics.get(getEpicById(idEpic)).add(subtask);
            checkStatusEpic();
        } else {
            System.out.println("Subtask не найдена");
        }
    }


    @Override
    public Epic createEpic(String name, String description, Instant startTime, long duration) {
        Epic epic = new Epic(name, description, generateId(), startTime, duration);
        epics.put(epic, null);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newId = generateId();
        epic.setId(newId);
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

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic)) {
            epics.put(epic, null);
            checkStatusEpic();
        } else {
            System.out.println("Epic не найден");
        }
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

    @Override
    public void updateTimeEpic(Epic epic) {
        List<SubTask> subtasks = getAllSubTasks();
        Instant startTime = subtasks.getFirst().getStartTime();
        Instant endTime = subtasks.getFirst().getEndTime();

        for (SubTask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime))
                startTime = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(endTime))
                endTime = subtask.getEndTime();
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        long duration = (endTime.toEpochMilli() - startTime.toEpochMilli());
        epic.setDuration(duration);
    }

    public boolean checkTime(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return true;
        }
        return prioritizedTasks.stream()
                .allMatch(taskSave ->
                        (taskSave.getStartTime() == null || taskSave.getEndTime() == null) ||
                                task.getEndTime().isBefore(taskSave.getStartTime()) ||
                                task.getStartTime().isAfter(taskSave.getEndTime()) ||
                                (task.getStartTime().isBefore(taskSave.getEndTime()) &&
                                        task.getEndTime().isAfter(taskSave.getStartTime()))
                );
    }

    private void validateTaskPriority() {
        List<Task> sortedTasks = new ArrayList<>(getPrioritizedTasks()); // Сохраняем порядок

        if (sortedTasks.size() < 2) {
            return;
        }

        for (int i = 1; i < sortedTasks.size(); i++) {
            Task currentTask = sortedTasks.get(i);
            Task previousTask = sortedTasks.get(i - 1);

            if (isTasksOverlapping(currentTask, previousTask)) {
                throw new ManagerException(
                        "Задачи #" + currentTask.getId() + " и #" + previousTask.getId() + " пересекаются"
                );
            }
        }
    }

    // Пример реализации метода для проверки пересечения задач
    private boolean isTasksOverlapping(Task task1, Task task2) {
        return !(task1.getStartTime() == null || task1.getEndTime() == null
                || task2.getStartTime() == null || task2.getEndTime() == null)
                && (task1.getStartTime().isBefore(task2.getEndTime())
                && task1.getEndTime().isAfter(task2.getStartTime()));
    }


    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validateTaskPriority();
    }

    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }
}
