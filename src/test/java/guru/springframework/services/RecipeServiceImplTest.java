package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {
    /* we are testing the recipe service so obviously we need to bring in an instance of that service and add it as a
    * property of the recipeServiceImplTest class
    * */
    RecipeServiceImpl recipeService;

    /* if we want to test this service we need an instance of recipeRepository because it is a required dependency of
    * the recipeServiceImpl class we
    * */
    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Before
    public void setUp() {
        /* Give me a mock recipeRepository */
        MockitoAnnotations.initMocks(this);
        /* create our recipe service which is dependent on the methods defined in recipeRepository */
        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipeByIdTest() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        Recipe recipeReturned = recipeService.findById(1L);

        assertNotNull("Null recipe returned", recipeReturned);
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void getRecipes() {
        // given
        /* create a set of recipes */
        Recipe recipe = new Recipe();
        HashSet recipesData = new HashSet();
        recipesData.add(recipe);

        //when
        /* tell mockito to return the set we just created when recipeRepository.findAll() is called*/
        when(recipeRepository.findAll()).thenReturn(recipesData);
        /* we want to get back a set of recipes */
        Set<Recipe> recipes = recipeService.getRecipes();

        // then
        assertEquals(recipes.size(), 1);
        /* when working with mocks we want to verify their interactions
        * we want to verify that the recipeRepository findAll() method was called once and only once
        *  */
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteById() throws Exception {
        // given
        Long idToDelete = Long.valueOf(2L);
        // when no 'when' clause when method has void return type
        recipeService.deleteById(idToDelete);
        // then
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }
}