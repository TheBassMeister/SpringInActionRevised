package tacos;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="Taco_Order")
public class Order {

    @Id @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private Date placedAt;
    @ManyToMany(targetEntity=Taco.class)
    private List<Taco> tacos=new ArrayList<>();

    @NotBlank(message="Name is required")
    private String customerName;
    @NotBlank(message="Street name is required")
    private String street;
    @NotBlank(message="City is required")
    private String city;
    @NotBlank(message="State is required")
    private String state;
    @Digits(integer = 5, fraction = 0,message="Invalid Zip")
    private String zip;
    @CreditCardNumber(message="Not a valid credit card number")
    private String ccNumber;
    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
            message="Must be formatted MM/YY")
    private String ccExpiration;
    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String ccCVV;

    @ManyToOne
    private User user;

    public void addDesign(Taco savedTaco) {
        tacos.add(savedTaco);
    }

    @PrePersist
    void placedAt() {
        this.placedAt = new Date();
    }
}
