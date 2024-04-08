package taskManager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> visitHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (visitHistory.size() >= 10) {
            visitHistory.remove(0);
        }
        visitHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return visitHistory;
    }
}
