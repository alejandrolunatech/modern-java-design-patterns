package com.alejandrolunatech.patterns.gof.command;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EditorTest {

    private final Editor editor = new Editor();

    @Test
    void undoAndRedoWalkTheHistory() {
        editor.execute(new Edit.Insert(0, "Hello world"));
        editor.execute(new Edit.Delete(5, 6));
        editor.execute(new Edit.Insert(5, ", Hoorn"));
        assertThat(editor.text()).isEqualTo("Hello, Hoorn");

        editor.undo();
        assertThat(editor.text()).isEqualTo("Hello");
        editor.undo();
        assertThat(editor.text()).isEqualTo("Hello world");
        editor.redo();
        assertThat(editor.text()).isEqualTo("Hello");
    }

    @Test
    void aNewEditClearsTheRedoHistory() {
        editor.execute(new Edit.Insert(0, "abc"));
        editor.undo();
        editor.execute(new Edit.Insert(0, "xyz"));

        assertThat(editor.redo()).isFalse();
        assertThat(editor.text()).isEqualTo("xyz");
    }

    @Test
    void undoOnAnEmptyHistoryDoesNothing() {
        assertThat(editor.undo()).isFalse();
        assertThat(editor.text()).isEmpty();
    }
}
