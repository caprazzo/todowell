package net.caprazzi.giddone.parsing;

import net.caprazzi.giddone.hook.PostReceiveHook;
import net.caprazzi.giddone.parsing.Repository;
import net.caprazzi.giddone.parsing.TodoRecord;

import java.util.Collection;
import java.util.Date;

public class TodoSnapshot {
    private Date created;
    private Repository repo;
    private Collection<TodoRecord> todo;

    public TodoSnapshot(Date created, PostReceiveHook hook, Collection<TodoRecord> todo) {
        this.created = created;
        // TODO: do the representation conversion outside
        this.repo = Repository.fromHook(hook);
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
