package ru.job4j.quartz;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class HabrCareerParse  {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    private static final int START_PAGE = 1;
    private static final int LAST_PAGE = 5;

    private String retrieveDescription(String link) throws IOException {
        String fullPage = String.format("%s%s", SOURCE_LINK, link);
        Document document = Jsoup.connect(fullPage).get();
        return  document.select(".vacancy-description__text").first().text();
    }

    public static void main(String[] args) throws IOException {
        for (int pageNumb = START_PAGE; pageNumb <= LAST_PAGE; pageNumb++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumb, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            DateTimeParser habrCParse = new Parser();
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element dateElement = row.select(".basic-date").first();
                String date = dateElement.attr("datetime");
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n ", vacancyName, link, habrCParse.parse(date));
            });
        }
    }
}
