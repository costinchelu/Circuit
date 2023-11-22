package ro.ase.costin.ecomcommon.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductExportDto {

    private int id;

    private String name;

    private String brand;

    private String category;

    private float cost;

    private float price;

    private float discount;

    private String image;
}
