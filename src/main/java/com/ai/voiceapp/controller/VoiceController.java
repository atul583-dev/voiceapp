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
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private OpenAiImageModel openAiImageModel;

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    @Autowired
    private OpenAiAudioSpeechModel openAiAudioSpeechModel;

    @GetMapping("/text-to-audio/{prompt}")
    public ResponseEntity<Resource> generateAudio(@PathVariable("prompt") String prompt) {
        OpenAiAudioSpeechOptions options
                = OpenAiAudioSpeechOptions.builder()
                .withModel("tts-1")
                .withSpeed(1.0f)
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .build();

        SpeechPrompt speechPrompt
                = new SpeechPrompt(prompt,options);

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

    @GetMapping("audio-to-text")
    public String generateTranscription() {

        OpenAiAudioTranscriptionOptions options
                = OpenAiAudioTranscriptionOptions.builder()
                .withLanguage("es")
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.SRT)
                .withTemperature(0f)
                .build();

        AudioTranscriptionPrompt prompt
                = new AudioTranscriptionPrompt(
                new FileSystemResource("/voiceapp/src/main/resources/harvard.wav"),
                options);

        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);

        return response.getResult().getOutput();
    }
}