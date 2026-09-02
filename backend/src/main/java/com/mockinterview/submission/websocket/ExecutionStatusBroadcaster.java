package com.mockinterview.submission.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExecutionStatusBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    public ExecutionStatusBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcast(String submissionId, ExecutionStatusMessage message) {
        messagingTemplate.convertAndSend("/topic/execution/" + submissionId, message);
    }
}
