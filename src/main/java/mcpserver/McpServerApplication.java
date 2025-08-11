package mcpserver;

import java.util.List;

import javax.tools.Tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {
	private static final Logger logger = LoggerFactory.getLogger(McpServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

	// @Bean
	// public List<ToolCallback> storeTools(mcpService mcpService){
	// 	return List.of(ToolCallbacks.from(mcpService));
	// }

	@Bean
	public ToolCallbackProvider storeTools(mcpService mcpService) {
		return MethodToolCallbackProvider.builder().toolObjects(mcpService).build();
	}
}
