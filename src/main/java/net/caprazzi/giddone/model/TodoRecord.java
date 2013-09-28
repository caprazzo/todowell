package net.caprazzi.giddone.model;

public class TodoRecord {
    private String file;
    private int line;
    private String label;
    private String body;

    public TodoRecord(String file, int line, String label, String body) {
        this.file = file;
        this.line = line;
        this.label = label;
        this.body = body;
    }

    public static TodoRecord from(Todo todo) {
        return new TodoRecord(
            todo.getComment().getSource().getRelativeFile().toString(),
            todo.getComment().getLineNumber(),
            todo.getLabel(),
            todo.getTodo());
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public String getLabel() {
        return label;
    }

    public String getBody() {
        return body;
    }
}
