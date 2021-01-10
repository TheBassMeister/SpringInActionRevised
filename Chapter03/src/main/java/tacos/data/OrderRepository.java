package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order,String> {

    Order save(Order order);

    List<Order> findByZip(String zip);
}
