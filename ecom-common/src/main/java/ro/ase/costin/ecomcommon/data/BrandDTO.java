package ro.ase.costin.ecomcommon.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BrandDTO {

    private int id;

    private String name;

    private String categories;
}
