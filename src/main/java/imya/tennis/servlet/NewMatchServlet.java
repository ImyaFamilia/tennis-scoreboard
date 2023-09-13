package imya.tennis.servlet;

import imya.tennis.ThymeleafRenderer;
import imya.tennis.model.Player;
import imya.tennis.service.ActiveMatchesService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import jakarta.validation.ConstraintViolationException;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThymeleafRenderer.renderFromRequest("new-match", request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("first-player");
        String secondName = request.getParameter("second-player");

        Player firstPlayer = new Player(firstName.toLowerCase());
        Player secondPlayer = new Player(secondName.toLowerCase());

        UUID uuid;
        try {
            uuid = ActiveMatchesService.createNewMatch(firstPlayer, secondPlayer);
        } catch (ConstraintViolationException e) {
            ThymeleafRenderer.fromRequest(request, response)
                .addVariableToContext("errors", e.getConstraintViolations())
                .build()
                .render("new-match");
            return;
        } catch (IllegalArgumentException e) {
            ThymeleafRenderer.fromRequest(request, response)
                .addVariableToContext("isSameNames", true)
                .build()
                .render("new-match");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/match/" + uuid);
    }
}
