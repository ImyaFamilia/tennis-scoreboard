package imya.tennis.servlet;

import imya.tennis.ThymeleafRenderer;
import imya.tennis.dto.response.MatchResponse;
import imya.tennis.service.ActiveMatchesService;
import imya.tennis.service.CurrentMatch;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match/*")
public class MatchScoreServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = null;

        try {
            uuid = UUID.fromString(request.getPathInfo().substring(1));
        } catch (IllegalArgumentException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/new-match");
            return;
        }

        if (ActiveMatchesService.hasMatch(uuid)) {
            CurrentMatch currentMatch = ActiveMatchesService.getMatch(uuid);
            MatchResponse matchResponse = new MatchResponse(currentMatch);

            ThymeleafRenderer.fromRequest(request, response)
                .addVariableToContext("currentMatch", matchResponse)
                .build()
                .render("/match");
        } else {
            response.sendRedirect(request.getContextPath() + "/new-match");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = null;

        try {
            uuid = UUID.fromString(request.getPathInfo().substring(1));
        } catch (IllegalArgumentException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/new-match");
            return;
        }

        if (ActiveMatchesService.hasMatch(uuid) && request.getParameter("player_id") != null) {
            CurrentMatch currentMatch = ActiveMatchesService.getMatch(uuid);

            try {
                synchronized (this) {
                    currentMatch.takeScore(Long.parseLong(request.getParameter("player_id")));
                }
            } catch (IllegalArgumentException e) {
                response.sendRedirect("/match/" + uuid);
                return;
            }

            if (currentMatch.isGameEnded()) {
                ActiveMatchesService.endMatch(uuid);
                response.sendRedirect(request.getContextPath() + "/matches/last");
            } else {
                response.sendRedirect(request.getContextPath() + "/match/" + uuid);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/new-match");
        }
    }
}
