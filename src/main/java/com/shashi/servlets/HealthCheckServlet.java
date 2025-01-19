package com.shashi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import com.shashi.utility.DBUtil;

@WebServlet("/health")
public class HealthCheckServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter pw = res.getWriter();
        res.setContentType("application/json");

        boolean dbConnected = DBUtil.isDatabaseConnected();
        if (dbConnected) {
            res.setStatus(HttpServletResponse.SC_OK);
            pw.println("{\"status\":\"UP\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pw.println("{\"status\":\"DOWN\"}");
        }
    }
}
