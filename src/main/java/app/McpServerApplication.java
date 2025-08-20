package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import app.internal.CartTool;
import app.internal.MenuTool;
import app.internal.OrderTool;
import app.internal.StoreTool;

@SpringBootApplication
public class McpServerApplication {
	private static final Logger logger = LoggerFactory.getLogger(McpServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider storeTools(MenuTool menuTool, StoreTool storeTool, CartTool cartTool, OrderTool orderTool) {
		return MethodToolCallbackProvider.builder().toolObjects(storeTool, menuTool, cartTool, orderTool).build();
	}
}