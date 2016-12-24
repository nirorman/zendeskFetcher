package main.java.com.nirorman.zendesk.impl;

import com.fasterxml.jackson.databind.JsonNode;
import main.java.com.nirorman.zendesk.api.FullJsonFetcher;
import main.java.com.nirorman.zendesk.api.SinglePageFetcher;
import main.java.com.nirorman.zendesk.exceptions.CreateHtmlFileException;
import main.java.com.nirorman.zendesk.exceptions.FetchJsonException;

import java.util.ArrayList;
import java.util.List;

import static main.java.com.nirorman.zendesk.utils.FileNamesUtil.*;

/**
 * Created by ormann on 21/12/2016.
 */
public class FullJsonFetcherImpl implements FullJsonFetcher {
    private String accountHost;
    private SinglePageFetcher singlePageFetcher;
    private HtmlCreatorImpl htmlCreator;
    private List<JsonNode> jsonNodeList;

    public FullJsonFetcherImpl(String host, String targetDirPath) {
        accountHost = host;
        singlePageFetcher = new SinglePageFetcherImpl();
        htmlCreator = new HtmlCreatorImpl(targetDirPath);
        jsonNodeList = new ArrayList<>();

    }
    public void fetch() {
        try {
            String url = String.format(URL_TEMPLATE, accountHost, CATEGORIES, SORT_RESULTS);
            while (!url.equals("null")) {
                JsonNode node = singlePageFetcher.fetchSinglePage(url);
                jsonNodeList.add(node); // don't dally with the response! store it and move on to get the next page
                url = node.get(NEXT_PAGE).asText();
            }
            for (JsonNode node:jsonNodeList) {
                htmlCreator.addJsonNodeToHtmlBody(node);
            }
            htmlCreator.createHtmlFile();
        } catch (FetchJsonException e) {
            System.out.println(String.format("Failed to fetch Json: %s", e.getMessage()));
        } catch (CreateHtmlFileException e) {
            System.out.println(String.format("failed to read HTML file: %s", e.getMessage()));
        } catch (Exception e) {
            System.out.println(String.format("Unexpected error: %s", e.getMessage()));
        }
    }
}
