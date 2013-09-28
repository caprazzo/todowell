package net.caprazzi.giddone;

import net.caprazzi.giddone.model.Clone;
import net.caprazzi.giddone.cloning.CloneService;
import net.caprazzi.giddone.deploy.DeployService;
import net.caprazzi.giddone.deploy.PresentationService;
import net.caprazzi.giddone.model.PostReceiveHook;
import net.caprazzi.giddone.model.CommentLine;
import net.caprazzi.giddone.model.Todo;
import net.caprazzi.giddone.model.TodoRecord;
import net.caprazzi.giddone.model.TodoSnapshot;
import net.caprazzi.giddone.parsing.*;
import net.caprazzi.giddone.worker.SourceFileScanner;

import java.util.Date;
import java.util.LinkedList;

public class GiddoneWorkerService {

    private final CloneService cloneService;
    private final SourceFileScanner scanner;
    private final CommentParser commentParser;
    private final TodoParser todoParser;
    private final PresentationService presentationService;
    private final DeployService deployService;

    public GiddoneWorkerService(CloneService cloneService, SourceFileScanner scanner, CommentParser commentParser, TodoParser todoParser, PresentationService presentationService, DeployService deployService) {
        this.cloneService = cloneService;
        this.scanner = scanner;
        this.commentParser = commentParser;
        this.todoParser = todoParser;
        this.presentationService = presentationService;
        this.deployService = deployService;
    }

    public void work(PostReceiveHook hook) throws Exception {
        Clone result = cloneService.clone(hook.getRepository().getCloneUrl(), hook.getBranch());

        if (result.isSuccess()) {
            Iterable<SourceFile> sourceFiles = scanner.scan(result.getCloneDir());
            Iterable<CommentLine> comments = commentParser.parse(sourceFiles);
            Iterable<Todo> todos = todoParser.parse(comments);
            LinkedList<TodoRecord> records = new LinkedList<TodoRecord>();
            for (Todo todo : todos) {
                records.add(TodoRecord.from(todo));
            }

            TodoSnapshot snapshot = new TodoSnapshot(new Date(), hook, records);

            String html = presentationService.asHtml(snapshot);

            deployService.deployHtmlPage(snapshot, html);
            cloneService.cleanUp(result);
        } else {
            throw new Exception("Worker aborted because cloneService failed");
        }

    }

}
