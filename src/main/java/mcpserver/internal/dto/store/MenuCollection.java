package mcpserver.internal.dto.store;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuCollection {
    private String menuId;
    private String name;
    private Integer price;
    private String description;
    private String category;
    private boolean isHidden = false;
}
