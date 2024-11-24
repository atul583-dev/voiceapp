package com.ai.voiceapp.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class VoiceController {

    @Autowired
    private OpenAiImageModel openAiImageModel;

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    @Autowired
    private OpenAiAudioSpeechModel openAiAudioSpeechModel;

    public ResponseEntity<Resource> generateAudio(String prompt) {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .withModel("tts-1")
                .withSpeed(1.0f)
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(prompt,options);
        SpeechResponse response = openAiAudioSpeechModel.call(speechPrompt);
        byte[] responseBytes = response.getResult().getOutput();
        ByteArrayResource byteArrayResource = new ByteArrayResource(responseBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("whatever.mp3")
                                .build().toString())
                .body(byteArrayResource);
    }

    @PostMapping("audio-to-text")
    public ResponseEntity<Resource> generateTranscription(@RequestParam("file") MultipartFile file) throws IOException {

        System.out.println("This is the test");
        // Configure transcription options
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .withLanguage("en")
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withTemperature(0f)
                .build();

        // Use the uploaded file as the resource for transcription
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new InputStreamResource(file.getInputStream()),
                options);

        // Call the transcription model
        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);
        String audioText = response.getResult().getOutput();

        System.out.println("Audio TO Text : " + audioText);
        String respo = chatModel.call(audioText);
        System.out.println("Response from GPT : " + respo);

        return generateAudio(respo);
    }
}