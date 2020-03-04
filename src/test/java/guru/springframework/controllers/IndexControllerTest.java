package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.ui.Model;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest {

    IndexController controller;

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    @Before
    public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      controller = new IndexController(recipeService);
    }

    @Test
    public void index() {
        //Given a HashSet of Recipes

        HashSet<Recipe> recipes =  new HashSet<>();
        Recipe recipe = new Recipe();
        Recipe recipe1 =  new Recipe();
        recipe.setId(1L);
        recipe1.setId(2L);
        recipes.add(recipe);
        recipes.add(recipe1);

        // when I call recipeService.getRecipes() return me a set of recipes
       when(recipeService.getRecipes()).thenReturn(recipes);

        /* here we are instantiating the argumentCaptor argument that will capture the nested type value of our returned
        * recipe Set
        * */
       ArgumentCaptor<Set<Recipe>> argumentCaptor = ArgumentCaptor.forClass(Set.class);

        // when the controller object calls the method index with the mock model argument
        String viewName = controller.index(model);

        // then I want you to verify that the returned string is the correct view name "index"
        assertEquals("index", viewName);
        // now I want you to verify that the recipeService object called getRecipes() only one time
        verify(recipeService, times(1)).getRecipes();
        /* now I want you to verify that the modelAddAttribute was called one time with the right key "recipes", and that
        * the type of set returned was a set of Recipes as captured by the argumentCaptor object*/
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
        // now we will instantiate a set of recipes and set it equal to the getValue() method of our argumentCaptor
        Set<Recipe> setInController = argumentCaptor.getValue();
        /* finally I want you to assert that the set we just assigned to the value of our argumentCaptor.getValue() method
        * is equal to the size of the hash set we created in the test*/
        assertEquals(2, setInController.size());
    }
}