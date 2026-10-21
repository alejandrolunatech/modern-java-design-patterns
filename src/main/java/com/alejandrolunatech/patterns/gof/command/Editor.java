package com.alejandrolunatech.patterns.gof.command;

import java.util.ArrayDeque;
import java.util.Deque;

/** The invoker: it runs commands and keeps the history for undo and redo. */
public final class Editor {

    private final StringBuilder buffer = new StringBuilder();
    private final Deque<Edit> undo = new ArrayDeque<>();
    private final Deque<Edit> redo = new ArrayDeque<>();

    public void execute(Edit edit) {
        undo.push(edit.applyTo(buffer));
        redo.clear();
    }

    public boolean undo() {
        return move(undo, redo);
    }

    public boolean redo() {
        return move(redo, undo);
    }

    private boolean move(Deque<Edit> from, Deque<Edit> to) {
        if (from.isEmpty()) {
            return false;
        }
        to.push(from.pop().applyTo(buffer));
        return true;
    }

    public String text() {
        return buffer.toString();
    }
}
