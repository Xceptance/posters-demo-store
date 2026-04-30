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
package com.xceptance.posters.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import jakarta.annotation.PostConstruct;
import jdk.jfr.FlightRecorder;

/**
 * JMX MBean representing the processing metrics of eCommerce orders.
 * This class captures business-level metadata such as order totals and item counts.
 * 
 * Exclusively created by AI (Gemini 3.1 Pro (High)).
 */
@Component
@ManagedResource(objectName = "com.xceptance.posters:type=JMX,name=OrderProcessingMetrics", description = "Metrics for checkout and order processing workflows.")
public class OrderProcessingMetrics 
{

    private final LongAdder totalOrders = new LongAdder();
    private final LongAdder totalItems = new LongAdder();
    private final DoubleAdder totalAmount = new DoubleAdder();
    
    private volatile String lastOrderId = "";
    private volatile String lastCreditCardVendor = "";

    @ManagedAttribute(description = "Total number of orders processed")
    public final long getTotalOrders() 
    {
        return totalOrders.sum();
    }

    @ManagedAttribute(description = "Total number of items processed")
    public final long getTotalItems() 
    {
        return totalItems.sum();
    }

    @ManagedAttribute(description = "Total amount of all orders")
    public final double getTotalAmount() 
    {
        return totalAmount.sum();
    }

    @ManagedAttribute(description = "The ID of the last processed order")
    public final String getLastOrderId() 
    {
        return lastOrderId;
    }

    @ManagedAttribute(description = "The credit card vendor of the last processed order")
    public final String getLastCreditCardVendor() 
    {
        return lastCreditCardVendor;
    }

    /**
     * Records a new order processing event.
     *
     * @param orderId          the ID of the order
     * @param itemCount        the number of items in the order
     * @param amount           the total order amount
     * @param creditCardVendor the credit card vendor used
     */
    public final void recordOrder(final String orderId, final int itemCount, final double amount, final String creditCardVendor) 
    {
        this.totalOrders.increment();
        this.totalItems.add(itemCount);
        this.totalAmount.add(amount);
        this.lastOrderId = orderId;
        this.lastCreditCardVendor = creditCardVendor;
    }
    
    @ManagedOperation(description = "Reset all order metrics")
    public final void resetMetrics() 
    {
        this.totalOrders.reset();
        this.totalItems.reset();
        this.totalAmount.reset();
        this.lastOrderId = "";
        this.lastCreditCardVendor = "";
    }

    @PostConstruct
    public final void registerPeriodicJfrEvent() 
    {
        FlightRecorder.addPeriodicEvent(com.xceptance.posters.jfr.OrderMetricsSummaryEvent.class, () -> 
        {
            com.xceptance.posters.jfr.OrderMetricsSummaryEvent event = new com.xceptance.posters.jfr.OrderMetricsSummaryEvent();
            event.totalOrders = this.totalOrders.sum();
            event.totalItems = this.totalItems.sum();
            event.totalAmount = this.totalAmount.sum();
            event.commit();
        });
    }
}
