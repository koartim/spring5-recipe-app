package guru.springframework.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotesTest {

    Notes note;

    @Before
    public void setUp() {
        note = new Notes();
    }

    @Test
    public void getId() {
        Long idValue = 4L;

        note.setId(idValue);

        assertEquals(idValue, note.getId());
    }
}