package com.alejandrolunatech.patterns.gof.command;

/**
 * Commands as immutable values. Applying one returns its inverse, so undo needs no extra state
 * inside the command.
 */
public sealed interface Edit {

    record Insert(int position, String text) implements Edit {
    }

    record Delete(int position, int length) implements Edit {
    }

    /** Applies the edit to the buffer and returns the edit that undoes it. */
    default Edit applyTo(StringBuilder buffer) {
        return switch (this) {
            case Insert insert -> {
                buffer.insert(insert.position(), insert.text());
                yield new Delete(insert.position(), insert.text().length());
            }
            case Delete delete -> {
                String removed = buffer.substring(delete.position(), delete.position() + delete.length());
                buffer.delete(delete.position(), delete.position() + delete.length());
                yield new Insert(delete.position(), removed);
            }
        };
    }
}
