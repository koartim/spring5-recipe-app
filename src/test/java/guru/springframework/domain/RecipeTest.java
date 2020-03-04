package guru.springframework.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecipeTest {

    Recipe recipe;

    @Before
    public void setUp() {
        recipe = new Recipe();
    }

    @Test
    public void getId() {
        Long idValue = 4L;

        recipe.setId(idValue);

        assertEquals(idValue, recipe.getId());
    }

    @Test
    public void getDescription() {
        String descriptionValue = "a tasty ham sandy";

        recipe.setDescription(descriptionValue);

        assertEquals(descriptionValue, recipe.getDescription());
    }
}