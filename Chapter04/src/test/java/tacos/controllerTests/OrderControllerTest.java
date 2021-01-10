package tacos.controllerTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import tacos.Ingredient;
import tacos.Order;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;
import tacos.web.OrderController;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepo;
    @MockBean
    private IngredientRepository ingredientRepo;

    @Before
    public void setup() {
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
    }


    @Test
    @WithMockUser
    public void openOrderForm() throws Exception{
        mockMvc.perform(get("/orders/current"))
                .andExpect(status().isOk());
    }

    @Test
    public void notAuthorized() throws Exception{
        mockMvc.perform(get("/orders/current"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "http://localhost/login"));
    }

    @Test
    @WithMockUser
    public void processOrder() throws Exception {
        mockMvc.perform(post("/orders")
                .content(createTestContent())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/orderCompleted"));
    }

    @Test
    @WithMockUser
    public void wrongCreditCardNumber() throws Exception {
        mockMvc.perform(post("/orders")
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
