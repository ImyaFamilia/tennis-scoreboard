package imya.tennis.servlet;

import imya.tennis.ThymeleafRenderer;
import imya.tennis.dto.response.MatchesResponse;
import imya.tennis.model.Match;
import imya.tennis.service.MatchesService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/matches/*")
public class MatchesServlet extends HttpServlet {
    int itemsPerPage = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filterName = request.getParameter("filter_name");
        String pageParam = null;

        try {
            pageParam = request.getPathInfo().substring(1);
        } catch (NullPointerException e) {
            response.sendRedirect(getURIRedirect(request.getContextPath() + "/matches/1",
                Map.of("filter_name", Optional.ofNullable(filterName))));
            return;
        }

        int pageNumber = 0;
        long countOfMatches = getCountOfMatches(filterName);
        long totalPages = getTotalPages(itemsPerPage, countOfMatches);

        if ("last".equals(pageParam)) {
            pageNumber = (int) totalPages;
        } else {
            try {
                pageNumber = Integer.parseInt(pageParam);
            } catch (IllegalArgumentException e) {
                response.sendRedirect(getURIRedirect(request.getContextPath() + "/matches/1",
                    Map.of("filter_name", Optional.ofNullable(filterName))));
                return;
            }
        }

        if (pageNumber > totalPages) {
            response.sendRedirect(getURIRedirect(request.getContextPath() + "/matches" + totalPages,
                Map.of("filter_name", Optional.ofNullable(filterName))));
            return;
        }

        List<Match> matches = MatchesService.getMatchesForPage(itemsPerPage, pageNumber, filterName);
        MatchesResponse matchesResponse = new MatchesResponse(matches, pageNumber, totalPages, countOfMatches);

        ThymeleafRenderer.fromRequest(request, response)
            .addVariableToContext("matches", matchesResponse)
            .build()
            .render("matches");
    }

    private long getCountOfMatches(String filterName) {
        if (filterName != null && !filterName.equals("")) {
            return MatchesService.getCountOfMatches(filterName);
        } else {
            return MatchesService.getCountOfMatches();
        }
    }

    private long getTotalPages(int itemsPerPage, long countOfMatches) {
        return MatchesService.calculateCountOfPages(itemsPerPage, countOfMatches);
    }

    private String getURIRedirect(String base, Map<String, Optional<String>> parameters) {
        URIBuilder uriBuilder = null;

        try {
            uriBuilder = new URIBuilder(base);

            for (Map.Entry<String, Optional<String>> pair : parameters.entrySet()) {
                if (pair.getValue().isPresent()) {
                    uriBuilder.addParameter(pair.getKey(), pair.getValue().get());
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Something went wrong when building URI for redirecting :/");
        }

        return uriBuilder.toString();
    }
}
