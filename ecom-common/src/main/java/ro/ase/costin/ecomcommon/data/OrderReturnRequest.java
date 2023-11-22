package ro.ase.costin.ecomcommon.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReturnRequest {

    private Integer orderId;

    private String reason;

    private String note;
}
