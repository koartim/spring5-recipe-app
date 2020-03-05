package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.midi.Receiver;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, IngredientCommandToIngredient ingredientCommandToIngredient, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }


    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        // recipe repository findById returns an Optional Object
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        // the recipeRespository.findById(recipeId) method will return an optional so we need handle a possible empty value
        if (!recipeOptional.isPresent()) {
            log.error("recipe id not found Id: " + recipeId);
        }
        // if the value is there we need to retrieve the recipe object in the optional
        Recipe recipe = recipeOptional.get();
        // then we need to get an ingredient command optional by creating a stream of the recipe ingredients set and filtering to find the
        // ingredient that matched the ingredientId that we passed in as a parameter
        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();

        if (!ingredientCommandOptional.isPresent()) {
            log.error("Ingredient Id not found: " + ingredientId);
        }
        return ingredientCommandOptional.get();
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand ingredientCommand) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(ingredientCommand.getRecipeId());

        if(!recipeOptional.isPresent()) {
            log.error("recipe not found for id: " + ingredientCommand.getRecipeId());
            return new IngredientCommand();
        } else {
            Recipe recipe =  recipeOptional.get();

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                    .findFirst();

            if(ingredientOptional.isPresent()) {
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(ingredientCommand.getDescription());
                ingredientFound.setAmount(ingredientCommand.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository
                .findById(ingredientCommand.getUom().getId())
                .orElseThrow(() -> new RuntimeException("UOM NOT FOUND")));
            } else {
                // add new ingredient
                Ingredient ingredient =  ingredientCommandToIngredient.convert(ingredientCommand);
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);

            }
            Recipe savedRecipe = recipeRepository.save(recipe);

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getDescription().equals(ingredientCommand.getDescription()))
                    .filter(recipeIngredients -> recipeIngredients.getAmount().equals(ingredientCommand.getAmount()))
                    .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(ingredientCommand.getUom().getId()))
                    .findFirst();

            return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        }
    }

    @Override
    public void deleteById(Long recipeId, Long id) {
       // the first thing we need to do is find the recipe with the ingredient we want to delete
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        // then we need to check if there is a value for the recipeId we entered
        if(recipeOptional.isPresent()) {
            log.debug("Found the recipe");
            Recipe recipe = recipeOptional.get();
         // now that we have the recipe we want, we need to look through recipe's ingredients set to find our ingredient
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream().filter(ingredient -> ingredient.getId().equals(id)).findFirst();
         // this will return us an optional, so we need to check if the value is present like we did with the recipe
         if (ingredientOptional.isPresent()) {
             log.debug("Found the ingredient");
             Ingredient ingredientToDelete = ingredientOptional.get();
             // we set the recipe value to null, so hibernate knows to remove it
             ingredientToDelete.setRecipe(null);
             // we then remove it from the recipe's set of ingredients
             recipe.getIngredients().remove(ingredientToDelete);
             // finally we save the recipe with the deleted ingredient removed
             recipeRepository.save(recipe);
          } else {
             log.debug("Recipe Id not found");
         }
        }

    }

}












