package taskmanager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private Map<Integer, Node> visitHistory;

    public InMemoryHistoryManager() {
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        tail.prev = head;
        visitHistory = new HashMap<>();
    }


    public void linkLast(Task task) {
        Node newNode = new Node(task);
        newNode.prev = tail.prev;
        newNode.next = tail;
        tail.prev.next = newNode;
        tail.prev = newNode;
        visitHistory.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    public void removeNode(Node node) {
        if (node != head && node != tail) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            visitHistory.remove(node.task.getId());
        }
    }


    @Override
    public void add(Task task) {
        if (visitHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        visitHistory.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        if (visitHistory.containsKey(id)) {
            removeNode(visitHistory.get(id));
            visitHistory.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
