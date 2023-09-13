package imya.tennis.servlet;

import imya.tennis.util.CookiesUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Map;

@WebServlet("/change-language")
public class LanguageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Cookie> cookieMap = CookiesUtils.getCookiesMap(request);

        Cookie cookie = cookieMap.getOrDefault("lang", new Cookie("lang", request.getLocale().getLanguage()));
        cookie.setMaxAge(32040000);

        if (cookie.getValue().equals("ru")) {
            cookie.setValue("en");
        } else {
            cookie.setValue("ru");
        }

        response.addCookie(cookie);

        if (request.getHeader("referer") != null) {
            response.sendRedirect(request.getHeader("referer"));
        } else {
            response.sendRedirect("/");
        }
    }
}
