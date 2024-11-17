package com.ai.voiceapp.service;

/*
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
*/

//@Service
public class VoiceService {

    /*private SpeechClient speechClient;
    final private RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.MP3).setLanguageCode("en-US").setSampleRateHertz(16000).build();

    @PostConstruct
    private void setUpRecognizer() {
        try {
            speechClient = SpeechClient.create();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String convertVoiceToText(byte[] audioData) throws IOException {

        String textResponse = null;
        ByteString byteString = ByteString.copyFrom(audioData);
        RecognitionAudio request = RecognitionAudio.newBuilder().setContent(byteString).build();

        try {
            RecognizeResponse response = speechClient.recognize(recognitionConfig, request);

            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternatives(0);
                System.out.println("Transcript: " + alternative.getTranscript());
                textResponse = textResponse + "\n";
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
        return textResponse;
    }*/
}
