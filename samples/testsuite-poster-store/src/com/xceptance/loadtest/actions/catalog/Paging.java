package com.xceptance.loadtest.actions.catalog;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.common.util.RegExUtils;
import com.xceptance.loadtest.util.AjaxUtils;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.util.XltRandom;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * This {@link AbstractHtmlPageAction} performs a paging.
 * 
 * @author sebastianloob
 */
public class Paging extends AbstractHtmlPageAction
{
    /**
     * Page number we start from.
     */
    private int currentPageNumber;

    /**
     * Page number we go to.
     */
    private int targetPageNumber;

    /**
     * The current page type. Possibilities are a top category overview page, a sub category overview page or a search
     * results overview page.
     */
    private String path;

    /**
     * The ID of the current category.
     */
    private String categoryId;

    /**
     * The javascript code for the paging functionality
     */
    private String scriptCodeAsString;

    private String getProductOfTopCategoryURL;

    private String getProductOfSubCategoryURL;

    private String getProductOfSearchURL;

    /**
     * Constructor
     * 
     * @param previousAction
     * @param timerName
     */
    public Paging(AbstractHtmlPageAction previousAction, String timerName)
    {
        super(previousAction, timerName);
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the current page.
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action", page);

        // Check the current page is a product overview page.
        Assert.assertTrue("No product overview page, so paging is impossible.",
                          HtmlPageUtils.isElementPresent(page, "id('productOverview')"));

        // get the current page number
        this.currentPageNumber = Integer.parseInt(HtmlPageUtils.findSingleHtmlElementByID(page, "productOverview")
                                                               .getAttribute("currentPage"));
        // the paging is build with Javascript, so we need to extract the information out of the paging javascript code
        HtmlElement scriptElement = HtmlPageUtils.findSingleHtmlElementByXPath(page, "id('main')/div/div/div[@id='pagination']/following-sibling::script");
        scriptCodeAsString = scriptElement.getTextContent();
        
        int beginIndex = scriptCodeAsString.indexOf("totalPages: ") + 12;
        int totalPageCount = Integer.parseInt(scriptCodeAsString.substring(beginIndex, scriptCodeAsString.indexOf(",", beginIndex)));
        this.targetPageNumber = XltRandom.nextInt(1, totalPageCount);

        // be sure, that the target page number is not the current page number
        Assert.assertFalse("The total page count is 1. Paging is not possible.",totalPageCount == 1);
        
        if (totalPageCount > 1)
        {
            while (this.currentPageNumber == this.targetPageNumber)
            {
                this.targetPageNumber = XltRandom.nextInt(1, totalPageCount);
            }
        }
        

        // get the path of the current URL to later extract the page type
        path = page.getUrl().getPath();

        categoryId = page.getUrl().getQuery().substring(11);
        
        // The javascript code also contains the URLs that we need to perform the subsequent ajax call to get the products of the next page.
        // Depending on the current page type we need one of the three extracted URLs
        List<String> URLStrings = RegExUtils.getAllMatches(scriptCodeAsString, "\\$\\.post\\('([^']*)", 1);  
        getProductOfTopCategoryURL = URLStrings.get(0);
        getProductOfSubCategoryURL = URLStrings.get(1);
        getProductOfSearchURL = URLStrings.get(2);

    }
    
    
    
    
    @Override
    protected void execute() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        
        // the request parameters of the AJAX call
        List<NameValuePair> pagingParams = new ArrayList<NameValuePair>();
        pagingParams.add(new NameValuePair("page", Integer.toString(targetPageNumber)));

        // execute the AJAX call and get the response
        WebResponse response = null;
        // the current page is a top category overview page
        if (path.contains("topCategory"))
        {
            pagingParams.add(new NameValuePair("categoryId", categoryId));
            response = AjaxUtils.callPost(page, getProductOfTopCategoryURL, pagingParams);
        }
        // the current page is a sub category overview page
        else if (path.contains("category"))
        {
            pagingParams.add(new NameValuePair("categoryId", categoryId));
            response = AjaxUtils.callPost(page, getProductOfSubCategoryURL, pagingParams);
        }
        // the current page shows some search results
        else if (path.contains("search"))
        {
            pagingParams.add(new NameValuePair("searchText",
                                               HtmlPageUtils.findSingleHtmlElementByID(page, "searchText").asText()));
            response = AjaxUtils.callPost(page, getProductOfSearchURL, pagingParams);
        }
        // unknown page type
        else
        {
            Assert.fail("Unknown page type.");
        }

        // update the page, show new products
        // remove current products from page
        HtmlElement productOverview = HtmlPageUtils.findSingleHtmlElementByID(page, "productOverview");
        productOverview.removeAllChildren();
        // get JSON object from response
        JSONObject jsonResponse = new JSONObject(response.getContentAsString());
        // get all products from JSON object
        JSONArray products = (JSONArray) jsonResponse.get("products");
        // remember the current index of the list
        HtmlElement ulElement = null;
        // render each product
        for (int i = 0; i < products.length(); i++)
        {
            JSONObject product = (JSONObject) products.get(i);
            // create a new row of products in the product grid
            if (i % 3 == 0)
            {
                ulElement = HtmlPageUtils.createHtmlElement("ul",
                                                            HtmlPageUtils.createHtmlElement("div", productOverview));
                ulElement.setAttribute("class", "thumbnails");
            }
            // add product as a list item
            HtmlElement productTag = HtmlPageUtils.createHtmlElement("div",
                                                                     HtmlPageUtils.createHtmlElement("li", ulElement));
            productTag.setId("product" + i);
            productTag.setAttribute("class", "thumbnail");
            // create link to product detail page
            HtmlElement productLink = HtmlPageUtils.createHtmlElement("a", productTag);
            HtmlElement contextPathScriptElement = HtmlPageUtils.findSingleHtmlElementByXPath(page, "html/head/script[@type='text/javascript' and contains(.,'CONTEXT_PATH')]");
            String contextPath = RegExUtils.getFirstMatch(contextPathScriptElement.getTextContent(), "CONTEXT_PATH = '(.*)';", 1);
            productLink.setAttribute("href",
                                     contextPath + "/productDetail/" + URLEncoder.encode(product.get("name").toString(), "UTF-8")
                                         + "?productId=" + product.get("id"));
            // add image tag
            HtmlElement imageTag = HtmlPageUtils.createHtmlElement("img", productLink);
            imageTag.setAttribute("src",
                    contextPath + product.get("imageURL"));
        }
        // set the current page number
        HtmlPageUtils.findSingleHtmlElementByID(page, "productOverview")
                     .setAttribute("currentPage", Integer.toString(this.targetPageNumber));
        // Publish the results.
        setHtmlPage(page);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getHtmlPage();

        // Basic checks - see action 'Homepage' for some more details how and when to use these validators
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);

        HeaderValidator.getInstance().validate(page);

        // Check the current page is a product overview page.
        Assert.assertTrue("Product Overview element missing.",
                          HtmlPageUtils.isElementPresent(page, "id('productOverview')"));

        // and we also see some poster's thumbnail images
        HtmlPageUtils.findHtmlElements(page, "id('productOverview')/div/ul/li/div[@class='thumbnail']");
    }
}
