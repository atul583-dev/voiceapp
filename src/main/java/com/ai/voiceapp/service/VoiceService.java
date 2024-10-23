package com.ai.voiceapp.service;

import com.google.cloud.speech.v1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VoiceService {

    // Convert voice (audio) to text using Google Cloud Speech-to-Text
    public String convertVoiceToText(byte[] audioData) throws IOException {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioData))
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            return response.getResultsList().get(0).getAlternativesList().get(0).getTranscript();
        }
    }

    // Analyze text (Simple rule-based example)
    public String analyzeText(String text) {
        if (text.contains("weather")) {
            return "The weather is sunny with a high of 25 degrees.";
        }
        return "Sorry, I couldn't understand your question.";
    }

    // Convert text to speech using Google Cloud Text-to-Speech
    public byte[] convertTextToVoice(String text) throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            return response.getAudioContent().toByteArray();
        }
    }
}
