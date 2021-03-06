package tacos.controllerTests;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tacos.Ingredient;
import tacos.Taco;
import tacos.User;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;
import tacos.data.UserRepository;
import tacos.web.DesignTacoController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static tacos.Ingredient.Type;



@RunWith(SpringRunner.class)
@WebMvcTest(DesignTacoController.class)
public class DesignTacoControllerTest {

    @Autowired
    private WebApplicationContext context;

    private UserDetailsService userDetailsService;
    private MockMvc mockMvc;

    @MockBean
    private IngredientRepository ingredientRepository;
    @MockBean
    private TacoRepository tacoRepository;
    @MockBean
    private UserRepository userRepository;

    private List<Ingredient> ingredients;
    private Taco design;
    private User testUser;

    @Before
    public void setup() {
        mockMvc= MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        ingredients = Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
                new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
                new Ingredient("CARN", "Carnitas", Type.PROTEIN),
                new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
                new Ingredient("LETC", "Lettuce", Type.VEGGIES),
                new Ingredient("CHED", "Cheddar", Type.CHEESE),
                new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
                new Ingredient("SLSA", "Salsa", Type.SAUCE),
                new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
        );

        when(ingredientRepository.findAll())
                .thenReturn(ingredients);

        when(ingredientRepository.findById("FLTO")).thenReturn(Optional.of(new Ingredient("FLTO", "Flour Tortilla", Type.WRAP)));
        when(ingredientRepository.findById("GRBF")).thenReturn(Optional.of(new Ingredient("GRBF", "Ground Beef", Type.PROTEIN)));
        when(ingredientRepository.findById("CHED")).thenReturn(Optional.of(new Ingredient("CHED", "Cheddar", Type.CHEESE)));

        design = new Taco();
        design.setName("Test Taco");

        design.setIngredients(Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
                new Ingredient("CHED", "Cheddar", Type.CHEESE)
        ));

        testUser=new User("testuser", "testpass", "Test User", "123 Street", "Someville", "CO", "12345", "123-123-1234");
        userRepository.save(testUser);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        userDetailsService= new InMemoryUserDetailsManager(Collections.singletonList(testUser));
    }

    @Test
    public void testShowDesignForm() throws Exception {
        mockMvc.perform(get("/design").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(model().attribute("wrap", ingredients.subList(0, 2)))
                .andExpect(model().attribute("protein", ingredients.subList(2, 4)))
                .andExpect(model().attribute("veggies", ingredients.subList(4, 6)))
                .andExpect(model().attribute("cheese", ingredients.subList(6, 8)))
                .andExpect(model().attribute("sauce", ingredients.subList(8, 10)))
                .andExpect(content().string(containsString("<h2>Feelin' hungry, <span>Test User</span>?</h2>")));
    }

    @Test
    public void processDesign() throws Exception {
        mockMvc.perform(post("/design").with(user(testUser))
                .content("name=Test+Taco&ingredients=FLTO,GRBF,CHED")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/orders/current"));
    }

    @Test
    public void noIngredientSelected() throws Exception {
        mockMvc.perform(post("/design").with(user(testUser))
                .content("name=Test+Taco")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(containsString("Please select at least one ingredient")));
    }

    @Test
    public void noNameGiven() throws Exception {
        mockMvc.perform(post("/design").with(user(testUser))
                .content("ingredients=FLTO,GRBF,CHED")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(containsString("must not be null")));
    }

    @Test
    public void nameTooShort() throws Exception {
        mockMvc.perform(post("/design").with(user(testUser))
                .content("name=Tes&ingredients=FLTO,GRBF,CHED")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(containsString("Name must be at least 5 characters long")));
    }

    @Test
    public void notAuthorized() throws Exception{
        mockMvc.perform(get("/design"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "http://localhost/login"));
    }

    @Test
    public void logout() throws Exception{
        mockMvc.perform(post("/logout").with(user(testUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/"));

    }

}
