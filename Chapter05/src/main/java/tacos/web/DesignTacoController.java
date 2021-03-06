package tacos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.*;
import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private IngredientRepository ingredientRepository;
    private TacoRepository tacoRepository;
    private User currentUser;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository){
        this.ingredientRepository=ingredientRepository;
        this.tacoRepository=tacoRepository;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        List<Ingredient> ingredients=new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }
    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }
    @ModelAttribute(name="user")
    public User user(){
        return currentUser;
    }


    @GetMapping
    public String showDesignForm(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute(TacoConstants.DESIGN, new Taco());
        currentUser=user;
        model.addAttribute("user", user());
        model.addAttribute("noTacos", true);
        return TacoConstants.DESIGN;
    }

    @PostMapping(params="action=addTaco")
    public String addToOrder(Model model, @ModelAttribute("design") @Valid Taco design, Errors errors, @ModelAttribute Order order) {
        if(errors.hasErrors()){
            return TacoConstants.DESIGN;
        }
        log.info("Processing design: " + design);
        Taco savedTaco=tacoRepository.save(design);
        order.addDesign(savedTaco);
        model.addAttribute("noTacos", false);
        return TacoConstants.DESIGN;
    }

    @PostMapping(params="action=orderTaco")
    public String submitOrder(@ModelAttribute Order order,  @RequestParam(value="action", required=true) String action) {
        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
