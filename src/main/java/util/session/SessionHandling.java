package util.session;

import java.util.Map;

import ninja.Context;
import ninja.session.SessionCookie;

public class SessionHandling {

	public static String getId(Context context) {
		String id = context.getSessionCookie().getId();
		return id;
	}
	
	public static Map<String, String> getData(Context context) {
		Map<String, String> data = context.getSessionCookie().getData();
		return data;
	}
	
	public static String getToken (Context context) {
		return context.getSessionCookie().getAuthenticityToken();
	}
	
	public static void put(Context context, String key, String value) {
		context.getSessionCookie().put(key, value);
	}
	
	public static void setUnknownUser (Context context) {
		SessionCookie cookie = context.getSessionCookie();
		if(cookie.get("user") == null) {
			cookie.put("user", "guest");
		}
	}
	
	public static void deleteCustomerId (Context context) {
		SessionCookie cookie = context.getSessionCookie();
		cookie.put("user", "guest");
	}
	
	public static void setOrderId (Context context, int orderId) {
		SessionCookie cookie = context.getSessionCookie();
		if(cookie.get("order") == null) {
			cookie.put("order", Integer.toString(orderId));
		}
	}
	
	public static int getOrderId (Context context) {
		int orderId;
		SessionCookie cookie = context.getSessionCookie();
		orderId = Integer.parseInt(cookie.get("order"));
		return orderId;
	}
	
	public static void setBasketId (Context context, int basketId) {
		SessionCookie cookie = context.getSessionCookie();
		if(cookie.get("basket") == null) {
			cookie.put("basket", Integer.toString(basketId));
		}
	}
	
	public static int getBasketId (Context context) {
		int basketId = -1;
		SessionCookie cookie = context.getSessionCookie();
		if(cookie.get("basket") != null) {
			basketId = Integer.parseInt(cookie.get("basket"));
		}
		return basketId;
	}
	
	public static String get(Context context, String key) {
		return context.getSessionCookie().get(key);
	}
	
	public static boolean isCustomerLogged (Context context) {
		
		boolean isLogged = true;
		SessionCookie cookie = context.getSessionCookie();
		if (cookie.get("user") == null || cookie.get("user").equals("guest")) {
			isLogged = false;
		}
		return isLogged;
	}

	public static void setCustomerId(Context context, int customerId) {
		SessionCookie cookie = context.getSessionCookie();
		cookie.put("user", Integer.toString(customerId));
	}
	
	public static int getCustomerId(Context context) {
		return Integer.parseInt(context.getSessionCookie().get("user"));
	}
	
}
