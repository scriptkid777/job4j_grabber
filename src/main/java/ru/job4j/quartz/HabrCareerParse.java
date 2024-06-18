package ru.job4j.quartz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    private static final int START_PAGE = 1;
    private static final int LAST_PAGE = 5;

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }


    private String retrieveDescription(String link) throws IOException {
        String fullPage = String.format("%s%s", SOURCE_LINK, link);
        Document document = Jsoup.connect(fullPage).get();
        return  document.select(".vacancy-description__text").first().text();
    }

   private Post postParse(Element row) throws IOException {
        Post post = new Post();
        Element firstVacTitle = row.select(".vacancy-card__title")
                .first()
                .child(0);
        post.setTitle(firstVacTitle.text());
        String titleRef = firstVacTitle.attr("href");
        post.setLink(titleRef);
        post.setDescription(retrieveDescription(titleRef));
        Element firstVacDate = row.select(".vacancy-card__date")
                .first()
                .child(0);
        String date = firstVacDate.attr("datetime");
        post.setCreated(dateTimeParser.parse(date));
        return post;
   }



    @Override
    public List<Post> list(String link)  throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int pageNumb = START_PAGE; pageNumb <= LAST_PAGE; pageNumb++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumb, SUFFIX);
            Document document;
            try {
                document = Jsoup.connect(fullLink).get();
                Elements vacancyCard = document.select(".vacancy-card__inner");
                for (Element element : vacancyCard) {
                    posts.add(postParse(element));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(posts);
        }
        return posts;
    }


    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse(new Parser());
        habrCareerParse.list(SOURCE_LINK);
    }
}
