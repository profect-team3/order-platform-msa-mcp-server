package mcpserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class McpServerApplicationTests {

	@MockitoBean
	ChatClient chatClient;

	@MockitoBean
	mcpService mcpService;

	@Test
	void contextLoads() {
	}

}
