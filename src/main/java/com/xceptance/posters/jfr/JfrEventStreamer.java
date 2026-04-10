/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xceptance.posters.jfr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import jdk.jfr.consumer.RecordingStream;

/**
 * Initiates JFR Event Streaming to monitor and intercept specific custom events in real-time.
 * 
 * Exclusively created by AI (Antigravity).
 */
@Component
public class JfrEventStreamer 
{

    private static final Logger log = LoggerFactory.getLogger(JfrEventStreamer.class);
    
    private RecordingStream recordingStream;

    @EventListener(ApplicationReadyEvent.class)
    public void startEventStream() 
    {
        log.info("Starting JFR Event Streaming for OrderProcessingEvents...");
        
        recordingStream = new RecordingStream();
        
        // Enable our custom event specifically, omitting stack traces for efficiency
        recordingStream.enable("com.xceptance.posters.OrderProcessing").withoutStackTrace();
        
        // Listen exclusively to our custom order event
        recordingStream.onEvent("com.xceptance.posters.OrderProcessing", event -> 
        {
            long durationMs = event.getDuration().toMillis();
            String orderId = event.getString("orderId");
            int itemCount = event.getInt("itemCount");
            String ccVendor = event.getString("creditCardVendor");
            
            // Real-time anomaly detection logic (alerting if processing takes > 3 seconds)
            if (durationMs > 3000) 
            {
                log.warn("[JFR ALERT] HIGH LATENCY DETECTED -> Order {} with {} items (Card: {}) took {}ms to process!", 
                          orderId, itemCount, ccVendor, durationMs);
            } 
            else 
            {
                log.info("[JFR TRACE] NORMAL LATENCY -> Order {} processed in {}ms", orderId, durationMs);
            }
        });

        // Starts the stream running asynchronously in a background thread
        recordingStream.startAsync();
    }
    
    @PreDestroy
    public void stopEventStream() 
    {
        if (recordingStream != null) 
        {
            log.info("Stopping JFR Event Streaming...");
            recordingStream.close();
        }
    }
}
