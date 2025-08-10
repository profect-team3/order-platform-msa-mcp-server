package mcpserver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

@Service
public class mcpService {
	private static final Logger log = LoggerFactory.getLogger(mcpService.class);
	private final List<GetStoreListResponse> stores = new ArrayList<>();

	@Tool(name = "get store info", description = "get store info")
	public List<GetStoreListResponse> getStores(){
		return stores;
	}

    @Tool(name = "get mcp chat response", description = "Provides a general chat response from the MCP server.")
    public String getMcpChatResponse(String query) {
        return "MCP Server's general response to: " + query;
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
