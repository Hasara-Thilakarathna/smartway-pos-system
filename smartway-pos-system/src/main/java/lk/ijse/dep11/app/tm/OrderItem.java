package lk.ijse.dep11.app.tm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderItem implements Serializable {
    private String item_code;
    private String item_description;
    private int qty;
    private BigDecimal unit_price;
    private transient JFXButton btnDelete;

    public BigDecimal getTotal() {
        return unit_price.multiply(new BigDecimal(qty)).setScale(2);
    }

}
