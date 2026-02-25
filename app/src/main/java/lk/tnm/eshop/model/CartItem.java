package lk.tnm.eshop.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String productId;
    private int quantity;
    private List<Attribute> attributes;


    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attribute{
        private String name;
        private  String value;
    }
}
