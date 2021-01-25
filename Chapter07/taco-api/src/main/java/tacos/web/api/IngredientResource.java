package tacos.web.api;

import static tacos.Ingredient.Type;

import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.CrossOrigin;
import tacos.Ingredient;


public class IngredientResource extends ResourceSupport {

    @Getter
    private String name;

    @Getter
    private Type type;


    public IngredientResource(Ingredient ingredient) {
        this.name = ingredient.getName();
        this.type = ingredient.getType();
    }
}
