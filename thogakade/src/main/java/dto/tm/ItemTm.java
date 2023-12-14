package dto.tm;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemTm extends RecursiveTreeObject<ItemTm> {
    private String code;
    private String Description;
    private double unitPrice;
    private int qtyOnHand;
    private Button btn;
}
