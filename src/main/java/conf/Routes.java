package conf;

import controllers.WebShopController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

	@Override
	public void init(Router router) {
		router.GET().route("/").with(WebShopController.class, "index");
		router.POST().route("/login").with(WebShopController.class, "login");
		router.GET().route("/logout").with(WebShopController.class, "logout");
		router.GET().route("/accountOverview").with(WebShopController.class, "accountOverview");
		router.GET().route("/orderOverview").with(WebShopController.class, "orderOverview");
//		router.GET().route("/paymentOverview").with(WebShopController.class, "paymentOverview");
//		router.GET().route("/settingOverview").with(WebShopController.class, "settingOverview");
//		router.GET().route("/addressOverview").with(WebShopController.class, "addressOverview");
		router.GET().route("/productDetail/{product}").with(WebShopController.class, "productDetail");
		router.GET().route("/topCategory/{topCategory}").with(WebShopController.class, "topCategoryOverview");
		router.GET().route("/category/{subCategory}").with(WebShopController.class, "productOverview");
		router.GET().route("/registration").with(WebShopController.class, "registration");
		router.POST().route("/registrationCompleted").with(WebShopController.class, "registrationCompleted");
		router.POST().route("/addToCart").with(WebShopController.class, "addToCart");
		router.GET().route("/basket").with(WebShopController.class, "basket");
		router.POST().route("/checkout").with(WebShopController.class, "checkout");
		router.POST().route("/deliveryAddressCompleted").with(WebShopController.class, "deliveryAddressCompleted");
		router.POST().route("/billingAddressCompleted").with(WebShopController.class, "billingAddressCompleted");
		router.POST().route("/paymentMethodCompleted").with(WebShopController.class, "paymentMethodCompleted");
		router.POST().route("/checkoutCompleted").with(WebShopController.class, "checkoutCompleted");
		
		router.GET().route("/assets/.*").with(AssetsController.class, "serve");
	}
}
