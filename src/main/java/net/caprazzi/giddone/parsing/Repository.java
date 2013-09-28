package net.caprazzi.giddone.parsing;

import net.caprazzi.giddone.model.PostReceiveHook;

public class Repository {
    private final String user;
    private final String repo;
    private final String url;
    private final String branch;

    public Repository(String user, String repo, String url, String branch) {
        this.user = user;
        this.repo = repo;
        this.url = url;
        this.branch = branch;
    }

    public String getUser() {
        return user;
    }

    public String getRepo() {
        return repo;
    }

    public String getUrl() {
        return url;
    }

    public String getBranch() {
        return branch;
    }

    public static Repository fromHook(PostReceiveHook hook) {
        return new Repository(hook.getRepository().getOwner().getName(), hook.getRepository().getName(), hook.getRepository().getCloneUrl(), hook.getBranch());
    }
}
