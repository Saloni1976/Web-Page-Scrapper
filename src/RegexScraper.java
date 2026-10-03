import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexScraper {

    public String getHtml(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public Set<String> findMatches(String text, String regex, boolean ignoreCase) {
        Set<String> results = new LinkedHashSet<>();

        Pattern pattern;
        if (ignoreCase) {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        } else {
            pattern = Pattern.compile(regex, Pattern.DOTALL);
        }

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (matcher.groupCount() >= 1) {
                results.add(matcher.group(1).trim());
            } else {
                results.add(matcher.group().trim());
            }
        }

        return results;
    }

    public Set<String> extractEmails(String html) {
        return findMatches(html, "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", false);
    }

    public Set<String> extractLinks(String html) {
        Set<String> allLinks = findMatches(html, "https?://[^\\s\"'<>]+", false);
        Set<String> cleanLinks = new LinkedHashSet<>();

        for (String link : allLinks) {
            String l = link.toLowerCase();

            if (!l.contains("schema.org")
                    && !l.contains("purl.org")
                    && !l.contains("w3.org")
                    && !l.contains("xmlns")
                    && !l.contains("rdfs")
                    && !l.contains("ogp")
                    && !l.contains("xmlschema")
                    && !l.endsWith(".css")
                    && !l.endsWith(".js")) {
                cleanLinks.add(link);
            }
        }

        return cleanLinks;
    }

    public Set<String> extractH1(String html) {
        Set<String> rawHeadings = findMatches(html, "<h1[^>]*>(.*?)</h1>", true);
        Set<String> cleanHeadings = new LinkedHashSet<>();

        for (String h : rawHeadings) {
            String cleaned = h.replaceAll("<.*?>", " ")
                              .replaceAll("\\s+", " ")
                              .trim();
            cleanHeadings.add(cleaned);
        }

        return cleanHeadings;
    }
}