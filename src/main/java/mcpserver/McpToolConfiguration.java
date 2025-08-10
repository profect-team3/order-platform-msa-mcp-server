package mcpserver;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfiguration {

    @Bean
    public ToolCallbackProvider customTools(mcpService mcpService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpService)
                .build();
    }
}
