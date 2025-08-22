package app.internal.dto.store;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class StoreCollection {
    private Long userId;
    private String storeKey;
    private String storeName;
    private String description;
    private List<String> categoryKeys;
    private Double avgRating;
    private Long reviewCount;
    private String phoneNumber;
    private Long minOrderAmount;
    private String address;
    private String regionName;
    private String regionFullName;
    private String storeAcceptStatus;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Long version;
    private List<MenuCollection> menus;
}
