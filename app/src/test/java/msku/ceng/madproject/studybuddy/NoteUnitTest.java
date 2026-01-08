package msku.ceng.madproject.studybuddy;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Local Unit Tests for the Note class.
 * These tests run on the local JVM and do not require an Android emulator.
 */
public class NoteUnitTest {

    // Test 1: Verifies that getters and setters work as expected
    @Test
    public void note_gettersAndSetters_workCorrectly() {
        // Arrange
        Note note = new Note();
        String expectedTitle = "Calculus 101";
        String expectedContent = "Derivatives and Integrals";
        String expectedUserId = "user123";

        // Act
        note.setTitle(expectedTitle);
        note.setContent(expectedContent);
        note.setUserId(expectedUserId);

        // Assert
        assertEquals("Title should match", expectedTitle, note.getTitle());
        assertEquals("Content should match", expectedContent, note.getContent());
        assertEquals("User ID should match", expectedUserId, note.getUserId());
    }

    // Test 2: Verifies that the constructor assigns values correctly
    @Test
    public void note_constructor_assignsValuesCorrectly() {
        // Arrange
        String id = "note_001";
        String title = "Physics";
        String content = "Newton's Laws";
        String userId = "student_99";

        // Act
        Note note = new Note(id, title, content, userId);

        // Assert
        assertNotNull("Note object should not be null", note);
        assertEquals("ID should match", id, note.getNoteId());
        assertEquals("Title should match", title, note.getTitle());
        assertEquals("Content should match", content, note.getContent());
        assertEquals("User ID should match", userId, note.getUserId());
    }
}