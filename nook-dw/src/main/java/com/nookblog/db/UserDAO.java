package com.nookblog.db;

import com.nookblog.core.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;

public class UserDAO {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> emailIndex = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public User create(User user) {
        Long id = idCounter.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        emailIndex.put(user.getEmail(), id);
        return user;
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public User findByEmail(String email) {
        Long id = emailIndex.get(email);
        return id != null ? users.get(id) : null;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void update(User user) {
        users.put(user.getId(), user);
    }

    public void delete(Long id) {
        User user = users.remove(id);
        if (user != null) {
            emailIndex.remove(user.getEmail());
        }
    }
}