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

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

/**
 * JFR Event representing the processing of an eCommerce order.
 * This class accurately captures business-level metadata such as order totals and item counts.
 * 
 * Exclusively created by AI (Antigravity).
 */
@Name("com.xceptance.posters.OrderProcessing")
@Label("Order Processing")
@Category({"Business", "eCommerce"})
@Description("Metrics for checkout and order processing workflows.")
public class OrderProcessingEvent extends Event 
{

    @Label("Order ID")
    @Description("The unique identifier of the placed order.")
    public String orderId;

    @Label("Item Count")
    @Description("Total number of items in the order.")
    public int itemCount;

    @Label("Total Amount")
    @Description("The total price of the order in the currency.")
    public double totalAmount;

    @Label("Credit Card Vendor")
    @Description("The vendor of the credit card used for payment.")
    public String creditCardVendor;
}
