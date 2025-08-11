package mcpserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class mcpService {
	private static final Logger log = LoggerFactory.getLogger(mcpService.class);
	private final List<GetStoreListResponse> stores = new ArrayList<>();

	@Tool(name = "get_store_info", description = "Get information about stores, optionally filtered by store name. Returns a formatted string of stores matching the criteria.")
	public String getStoreInfo(String storeName){
		List<GetStoreListResponse> filteredStores;
		if (storeName == null || storeName.isEmpty()) {
			filteredStores = stores;
		} else {
			filteredStores = stores.stream()
				.filter(store -> store.getStoreName().toLowerCase().contains(storeName.toLowerCase()))
				.collect(Collectors.toList());
		}
		if (filteredStores.isEmpty()) {
			return "No stores found with that name.";
		}

		return filteredStores.stream()
			.map(store -> String.format("""
                    Store Name: %s,
                    Address: %s,
                    Minimum Order Amount: %d,
                    Average Rating: %.1f
                    """, store.getStoreName(), store.getAddress(), store.getMinOrderAmount(), store.getAverageRating()))
			.collect(Collectors.joining("\n"));
	}

	@PostConstruct
	public void init() {
		stores.add(GetStoreListResponse.builder()
			.storeId(UUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"))
			.storeName("교촌치킨")
			.address("서울시 강남구")
			.minOrderAmount(15000L)
			.averageRating(3.5)
			.build());
		stores.add(GetStoreListResponse.builder()
			.storeId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
			.storeName("BHC치킨")
			.address("서울시 서초구")
			.minOrderAmount(18000L)
			.averageRating(4.0)
			.build());
		stores.add(GetStoreListResponse.builder()
			.storeId(UUID.fromString("a1b2c3d4-e5f6-7890-9abc-def012345678"))
			.storeName("굽네치킨")
			.address("서울시 송파구")
			.minOrderAmount(16000L)
			.averageRating(4.2)
			.build());
		log.info("Initialized stores: {}", stores);
	}
}