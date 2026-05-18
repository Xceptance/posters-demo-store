package com.xceptance.posters;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class TestThymeleaf {
    public static void main(String[] args) {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        Context context = new Context();
        context.setVariable("id", "123");
        String result = templateEngine.process("<form th:hx-post=\"@{http://example.com/test/{id}(id=${id})}\"></form>", context);
        System.out.println("RESULT: " + result);
    }
}
