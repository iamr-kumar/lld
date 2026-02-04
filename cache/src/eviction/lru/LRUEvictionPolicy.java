package cache.src.eviction.lru;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cache.src.eviction.IEvictionPolicy;

class Node<K> {
    K key;
    Node<K> prev;
    Node<K> next;

    Node(K key) {
        this.key = key;
    }
}

public class LRUEvictionPolicy<K> implements IEvictionPolicy<K> {
    private Node<K> head;
    private Node<K> tail;
    private Map<K, Node<K>> nodeMap;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public LRUEvictionPolicy() {
        head = new Node<>(null); // Dummy head
        tail = new Node<>(null); // Dummy tail
        head.next = tail;
        tail.prev = head;
        nodeMap = new java.util.HashMap<>();
    }

    private void addToFront(K key) {
        Node<K> node = new Node<>(key);
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        nodeMap.put(key, node);
    }

    private void moveToFront(Node<K> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeTail() {
        if (tail.prev == head) {
            return; // List is empty
        }
        Node<K> nodeToRemove = tail.prev;
        nodeToRemove.prev.next = tail;
        tail.prev = nodeToRemove.prev;
        nodeMap.remove(nodeToRemove.key);
    }

    @Override
    public void onAccess(K key) {
        Node<K> node = nodeMap.get(key);
        if (node != null) {
            moveToFront(node);
        }
    }

    @Override
    public void onInsert(K key) {
        rwLock.writeLock().lock();
        try {
            if (!nodeMap.containsKey(key)) {
                addToFront(key);
            } else {
                onAccess(key);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public K evict() {
        rwLock.writeLock().lock();
        try {

            if (tail.prev == head) {
                return null; // List is empty
            } else {
                K evictedKey = tail.prev.key;
                removeTail();
                return evictedKey;
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public void onRemove(K key) {
        rwLock.writeLock().lock();
        try {
            Node<K> node = nodeMap.get(key);
            if (node != null) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                nodeMap.remove(key);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}