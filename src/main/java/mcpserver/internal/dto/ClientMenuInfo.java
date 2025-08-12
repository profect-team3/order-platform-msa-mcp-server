package mcpserver.internal.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientMenuInfo {
    private UUID menuId;
    private String name;
    private String description;
    private Long price;
}
