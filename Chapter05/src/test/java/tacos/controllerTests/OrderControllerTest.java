package tacos.controllerTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
import tacos.data.TacoRepository;
import tacos.web.OrderController;
import tacos.web.OrderProps;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
@EnableConfigurationProperties(value = OrderProps.class)
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private OrderProps orderProps;

    private MockMvc mockMvc;
    private UserDetailsService userDetailsService;
    private User testUser;
    private Order orderWithoutTacos;
    private List<Order> lastOrders=new ArrayList<>();

    @MockBean
    private OrderRepository orderRepo;
    @MockBean
    private TacoRepository tacoRepo;
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


        for (long l = 0L; l < orderProps.getPageSize()+5; l++) {
            lastOrders.add(createNewOrder(l));
        }

        when(orderRepo.findByUserOrderByPlacedAtDesc(testUser, PageRequest.of(0, orderProps.getPageSize())))
                .thenReturn(lastOrders.subList(0, orderProps.getPageSize()));

        when(tacoRepo.save(any(Taco.class))).thenReturn(design);
    }

    private Order createNewOrder(long id){
        Taco taco1=new Taco();
        taco1.setName("TestTaco"+id);
        taco1.setIngredients(Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE)
        ));
        taco1.setCreatedAt(new Date());
        taco1.setId(1L);
        Order newOrder=new Order();
        newOrder.setCustomerName(testUser.getFullname());
        newOrder.setStreet(testUser.getStreet());
        newOrder.setCity(testUser.getCity());
        newOrder.setState(testUser.getState());
        newOrder.setZip(testUser.getZip());
        newOrder.addDesign(taco1);
        newOrder.setUser(testUser);
        orderRepo.save(newOrder);

        return newOrder;
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

    @Test
    public void getLastOrders()throws Exception{
        mockMvc.perform(get("/orders/lastOrders").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("orders", hasSize(orderProps.getPageSize())));
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
