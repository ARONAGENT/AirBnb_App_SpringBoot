package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.services.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    @Override
    public String askAi(String topic){
        var response=chatClient.prompt()
                .user(topic)
                .advisors()
                .call()
                .chatClientResponse();
        return response.chatResponse().getResult().getOutput().getText();
    }
}
