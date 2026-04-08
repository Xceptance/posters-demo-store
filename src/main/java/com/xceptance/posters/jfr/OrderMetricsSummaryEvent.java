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
import jdk.jfr.Period;
import jdk.jfr.StackTrace;

/**
 * JFR Event representing the aggregated metrics of eCommerce orders.
 * Periodically captures business-level metadata such as total order counts and amounts.
 * 
 * Exclusively created by AI (Gemini 3.1 Pro (High)).
 */
@Name("com.xceptance.posters.OrderMetricsSummary")
@Label("Order Metrics Summary")
@Category({"Business", "eCommerce"})
@Description("Aggregated summary metrics for checkout and order processing workflows.")
@Period("10 s")
@StackTrace(false)
public class OrderMetricsSummaryEvent extends Event 
{

    @Label("Total Orders")
    @Description("Total number of orders processed.")
    public long totalOrders;

    @Label("Total Items")
    @Description("Total number of items processed.")
    public long totalItems;

    @Label("Total Amount")
    @Description("The total sum amount of all orders.")
    public double totalAmount;
}
