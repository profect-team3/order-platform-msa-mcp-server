package mcpserver.internal.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuInfo {
    private String menuId;
    private String menuName;
    private String description;
    private Integer price;
}
