package com.ai.voiceapp.controller;

import com.ai.voiceapp.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    // Endpoint to handle audio input
    @PostMapping("/process")
    public ResponseEntity<byte[]> processVoice(@RequestParam("audio") MultipartFile audioFile) {
        try {
            byte[] audioData = audioFile.getBytes();

            // Step 1: Convert Voice to Text
            String transcript = voiceService.convertVoiceToText(audioData);

            // Step 2: Analyze Text and Generate a Response
            String responseText = voiceService.analyzeText(transcript);

            // Step 3: Convert Response Text to Voice
            byte[] voiceResponse = voiceService.convertTextToVoice(responseText);

            // Return the generated voice response
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "audio/mpeg");
            return new ResponseEntity<>(voiceResponse, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
