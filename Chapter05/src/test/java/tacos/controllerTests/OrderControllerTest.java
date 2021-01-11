package tacos.controllerTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tacos.Ingredient;
import tacos.Order;
import tacos.Taco;
import tacos.User;
import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;
import tacos.web.OrderController;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private UserDetailsService userDetailsService;
    private User testUser;
    private Order orderWithoutTacos;

    @MockBean
    private OrderRepository orderRepo;
    @MockBean
    private IngredientRepository ingredientRepo;

    @Before
    public void setup() {
        mockMvc= MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Order order=new Order();

        Taco design = new Taco();
        design.setName("Test Taco");

        design.setIngredients(Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE)
        ));
        order.addDesign(design);
        orderRepo.save(order);

        testUser=new User("testuser", "testpass", "Test User", "123 Street", "Someville", "CO", "12345", "123-123-1234");
        userDetailsService= new InMemoryUserDetailsManager(Collections.singletonList(testUser));

        orderWithoutTacos=new Order();
        orderWithoutTacos.setCustomerName(testUser.getFullname());
        orderWithoutTacos.setStreet(testUser.getStreet());
        orderWithoutTacos.setCity(testUser.getCity());
        orderWithoutTacos.setState(testUser.getState());
        orderWithoutTacos.setZip(testUser.getZip());
    }


    @Test
    public void openOrderForm() throws Exception{
        mockMvc.perform(get("/orders/current").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("order", orderWithoutTacos));
    }

    @Test
    public void notAuthorized() throws Exception{
        mockMvc.perform(get("/orders/current"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "http://localhost/login"));
    }

    @Test
    public void processOrder() throws Exception {
        mockMvc.perform(post("/orders").with(user(testUser))
                .content(createTestContent())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/orderCompleted"));
    }

    @Test
    public void wrongCreditCardNumber() throws Exception {
        mockMvc.perform(post("/orders").with(user(testUser))
                .content(createTestContent("34567"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(containsString("Not a valid credit card number")));
    }

    private String createTestContent(){
        var testCreditCardNumber="378618187748325"; //Not a real Credit Card Number
        return createTestContent(testCreditCardNumber);
    }

    private String createTestContent(String creditCardNumber){
        StringBuilder sb=new StringBuilder("customerName=TestUser&");
        sb.append("street=TestStreet&");
        sb.append("city=TestCity&");
        sb.append("state=AZ&");
        sb.append("zip=32145&");
        sb.append("ccNumber="+creditCardNumber+"&");//Test number of a not real card
        sb.append("ccExpiration=09/22&");
        sb.append("ccCVV=321");
        return sb.toString();
    }
}
