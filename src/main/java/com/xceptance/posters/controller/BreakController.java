package com.xceptance.posters.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xceptance.posters.config.StatusConf;

/**
 * Handles the "break" feature: toggling intentional bugs for testing/demo.
 */
@Controller
public class BreakController
{
    private final StatusConf statusConf;

    public BreakController(StatusConf statusConf)
    {
        this.statusConf = statusConf;
    }

    @GetMapping("/break")
    public String breakPage(Model model)
    {
        model.addAttribute("status", statusConf.getStatus());
        return "break/break";
    }

    @PostMapping("/break/toggle")
    @ResponseBody
    public Map<String, Object> toggle(@RequestParam String feature,
                                       @RequestParam boolean enabled,
                                       @RequestParam(required = false, defaultValue = "0") int value,
                                       @RequestParam(required = false, defaultValue = "") String text)
    {
        switch (feature)
        {
            case "categoriesBroken" -> statusConf.setCategoriesBroken(enabled);
            case "cartQuantitiesChange" -> statusConf.setCartQuantitiesChange(enabled);
            case "ordersBlocked" -> statusConf.setOrdersBlocked(enabled);
            case "searchResultsChanged" -> statusConf.setSearchResultsChanged(enabled);
            case "cartProductMixups" -> statusConf.setCartProductMixups(enabled);
            case "openLogin" -> statusConf.setOpenLogin(enabled);
            case "orderHistoryMessy" -> statusConf.setOrderHistoryMessy(enabled);
            case "wrongLocale" -> statusConf.setWrongLocale(enabled);
            case "productBlock" -> { statusConf.setProductBlock(enabled); statusConf.setBlockedId(value); }
            case "productOrderBlock" -> { statusConf.setProductOrderBlockWhen(enabled); statusConf.setIncludedId(value); }
            case "searchCounterWrong" -> { statusConf.setSearchCounterWrongBy(enabled); statusConf.setCounterAdjustment(value); }
            case "blockSearch" -> { statusConf.setBlockSearchWhen(enabled); statusConf.setBlockedTerm(text); }
            case "cartLimit" -> { statusConf.setCartLimit(enabled); statusConf.setLimitMax(value); }
            case "cartLimitTotal" -> { statusConf.setCartLimitTotal(enabled); statusConf.setLimitTotal(value); }
        }
        return statusConf.getStatus();
    }

    @PostMapping("/break/disableAll")
    @ResponseBody
    public Map<String, Object> disableAll()
    {
        statusConf.disableAll();
        return statusConf.getStatus();
    }

    @GetMapping("/break/status")
    @ResponseBody
    public Map<String, Object> status()
    {
        return statusConf.getStatus();
    }
}
