package net.caprazzi.todowell;

import java.util.Collection;
import java.util.Date;

public class TodoSnapshot {
    private Date created;
    private Repository repo;
    private Collection<TodoRecord> todo;

    public TodoSnapshot(Date created, Repository repo, Collection<TodoRecord> todo) {
        this.created = created;
        this.repo = repo;
        this.todo = todo;
    }

    public Date getCreated() {
        return created;
    }

    public Repository getRepo() {
        return repo;
    }

    public Collection<TodoRecord> getTodo() {
        return todo;
    }
}
