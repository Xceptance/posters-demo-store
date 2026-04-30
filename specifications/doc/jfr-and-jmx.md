# JFR and JMX Observability in Posters Demo Store

This document outlines the Java Flight Recorder (JFR) events and Java Management Extensions (JMX) MBeans implemented to monitor and track order processing workflows within the application. 

These observability tools allow operators and developers to gain insights into business-level operations, track aggregates over time, and inspect point-in-time metrics without significant application overhead.

## Architecture and Design

The monitoring strategy employs three discrete, but complementary, components to cover the full spectrum of observability:

1. **JFR Action Event (`OrderProcessingEvent`)**: Captures point-in-time details individually for every checkout event.
2. **JMX MBean (`OrderProcessingMetrics`)**: Aggregates point-in-time actions into running totals and exposes this live state globally.
3. **JFR Periodic Event (`OrderMetricsSummaryEvent`)**: A periodic polling event bridging the gap between point-in-time events and global state by capturing snapshots of the MBean aggregations.

---

## 1. JFR Point-in-Time Event: OrderProcessingEvent

The `OrderProcessingEvent` provides a direct insight into individual order processing workflows natively via JDK Flight Recorder.

- **Class:** `com.xceptance.posters.jfr.OrderProcessingEvent`
- **Category:** `Business`, `eCommerce`
- **Label:** `Order Processing`
- **Name:** `com.xceptance.posters.OrderProcessing`
- **Emission:** Yielded synchronously upon checkout completion in `CheckoutService`.

### Captured Data (Per Event):
| Field | Type | Description |
| :--- | :--- | :--- |
| `orderId` | `String` | The unique identifier / number of the placed order. |
| `itemCount` | `int` | Total number of individual items within the order. |
| `totalAmount` | `double` | The total revenue amount of the transaction. |
| `creditCardVendor` | `String` | The provider/vendor of the credit card used. |

---

## 2. JMX Bean: OrderProcessingMetrics

The `OrderProcessingMetrics` component is an active MBean component used to securely expose aggregate statistics continuously to JMX monitoring clients.

- **Class:** `com.xceptance.posters.jmx.OrderProcessingMetrics`
- **JMX ObjectName:** `com.xceptance.posters:type=JMX,name=OrderProcessingMetrics`
- **Lifecycle:** Exists as a singleton Spring bean tracking interactions asynchronously.

### Exposed Attributes:
| Attribute | Type | Description |
| :--- | :--- | :--- |
| `TotalOrders` | `long` | Total running sum of all submitted orders since startup. |
| `TotalItems` | `long` | Total running sum of all grouped items placed since startup. |
| `TotalAmount` | `double` | Total sum equivalent of processed revenue. |
| `LastOrderId` | `String` | Snapshot reference to the order number processing last. |
| `LastCreditCardVendor` | `String` | Snapshot reference to the last credit provider used. |

### Exposed Operations:
| Operation | Parameters | Description |
| :--- | :--- | :--- |
| `resetMetrics()` | None | Immediately wipes and zeros the live aggregate counters and strings. |

---

## 3. JFR Periodic Event: OrderMetricsSummaryEvent

While `OrderProcessingEvent` fires explicitly per-transaction, `OrderMetricsSummaryEvent` runs independently by periodically scraping the global JMX metric aggregates into JFR on a time interval.

- **Class:** `com.xceptance.posters.jfr.OrderMetricsSummaryEvent`
- **Category:** `Business`, `eCommerce`
- **Label:** `Order Metrics Summary`
- **Name:** `com.xceptance.posters.OrderMetricsSummary`
- **Period Threshold**: `@Period("10 s")` 
- **Emission:** JFR engine initiates the scrape automatically. The `FlightRecorder.addPeriodicEvent()` is registered within the MBean `@PostConstruct` initialization.

### Captured Data (Per Snapshot):
| Field | Type | Description |
| :--- | :--- | :--- |
| `totalOrders` | `long` | Captured baseline of total processed orders. |
| `totalItems` | `long` | Captured baseline of total processed items. |
| `totalAmount` | `double` | Captured baseline amount of revenue. |

> **Why a periodic event?** Emitting long-lived global counters per-transaction creates redundant metadata. Polling the MBean global state periodically reduces overhead, and produces cleaner JFR recordings suitable for historical analytics and aggregations matching typical enterprise metric dashboards.
