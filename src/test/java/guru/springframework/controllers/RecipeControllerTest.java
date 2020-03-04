package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerTest {

    @Mock
    RecipeService recipeService;

    RecipeController recipeController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        recipeController = new RecipeController(recipeService);
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    public void testGetRecipe() throws Exception {
        // given a new recipe object with Long id
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        // When the findById method is called within the context of the recipe controller
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
        when(recipeService.findById(anyLong())).thenReturn(recipe);
        mockMvc.perform(get("/recipe/1/show"))
          // then expect that the status will return 200 OK, the view name will be recipe/show, and the model will have the attribute "recipe"
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/show"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testGetNewRecipeForm() throws Exception {

        // given a recipe command object
        RecipeCommand command = new RecipeCommand();
        // when the controller is called with the request map "/recipe/new"
        mockMvc.perform(get("/recipe/new"))
                // then expect a 200 status OK, the view name to be "recipe/recipeform" and the model should have the attribute "recipe"
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));

    }

    @Test
    public void testPostNewRecipeForm() throws Exception {
        // given a Recipe command object with a long id of 2L
        RecipeCommand command = new RecipeCommand();
        command.setId(2L);
        // when the recipeService.saveRecipeCommand is called on any object

        when(recipeService.saveRecipeCommand(any())).thenReturn(command);

        // then expect a post request from the controller with the request path "/recipe", expect an encoded url form
        // there should be an id for a specific recipe, as well as a params specifying a key, and a value ie " {description : "some string"}
        // request status shoud be a 302 redirect, and the view name returned after post should be to a redirect path
        mockMvc.perform(post("/recipe")
                // MediaType.APPLICATION_FORM_URLENCODED this will mimic a form post
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "some string")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/show"));
    }


    // we expect the controller to return a populated object view
    @Test
    public void testGetUpdateView() throws Exception {
        // given a recipeCommand object with and id Long 2
        RecipeCommand command = new RecipeCommand();
        command.setId(2L);

        // when recipeService.findCommandById(andLongId) is called , then a command object should be returned
        when(recipeService.findCommandById(anyLong())).thenReturn(command);
        // the controller should performa  get request to the "/recipe/1/update" path, the status should be 200 OK, and the view name
        // should be "recipe/recipeform", the model should have an attribue "recipe" on it.
        mockMvc.perform(get("/recipe/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testDeleteAction() throws Exception{
        mockMvc.perform(get("/recipe/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(recipeService, times(1)).deleteById(anyLong());
    }
}