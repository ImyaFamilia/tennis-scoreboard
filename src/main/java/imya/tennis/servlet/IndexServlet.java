package imya.tennis.servlet;

import imya.tennis.ThymeleafRenderer;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebServlet("")
@Slf4j
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThymeleafRenderer.renderFromRequest("index", request, response);
    }
}
