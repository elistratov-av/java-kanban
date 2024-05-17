package services;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node<Task>> map = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        ++size;
    }

    private void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        --size;
    }

    private List<Task> getTasks() {
        List<Task> results = new ArrayList<>(size);
        for (Node<Task> node = head; node != null; node = node.next) {
            results.add(node.data);
        }
        return results;
    }

    @Override
    public void add(Task task) {
        Node<Task> node = map.get(task.getId());
        if (node != null)
            removeNode(node);
        linkLast(task);
        map.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = map.remove(id);
        if (node != null)
            removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}

