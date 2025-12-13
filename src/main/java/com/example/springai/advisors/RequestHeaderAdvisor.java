package com.example.springai.advisors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class RequestHeaderAdvisor implements CallAdvisor, Ordered, RequestHeaders {

    @Override
    public int getOrder() {
        // Lower values execute first. Ensure this runs before other advisors if needed.
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String userId = getUserIdFromRequestHeaders();
        //StringUtils.isNotBlank(username)) advisorSpec.param(ChatMemory.CONVERSATION_ID, username);
        if ( StringUtils.isNotBlank(userId) ) {
            // Option 1: Add the value to the advisor context for other advisors/tools to use
            request.context().put(ChatMemory.CONVERSATION_ID, userId);
            // Option 2 (less common): Modify the prompt/messages in the ChatClientRequest
            // e.g., request.getPrompt().getMessages().add(new SystemMessage("User ID is: " + userId));
        }

        // Continue the advisor chain
        return chain.nextCall(request);
    }

    private String getUserIdFromRequestHeaders() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest httpRequest = attributes.getRequest();
                // Retrieve the specific header value
                return httpRequest.getHeader(USER_ID_HEADER);
            }
        } catch (IllegalStateException e) {
            // Handle cases where there is no active HTTP request context (e.g., in a non-web test)
            log.error("No active request context: {}", e.getMessage(), e);
        }
        return null;
    }
}