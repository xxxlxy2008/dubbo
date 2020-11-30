package com.demo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.jsonrpc4j.JsonRpcServer;

/**
 * Created on 2020-08-11
 */
public class RpcServlet extends HttpServlet {
    private JsonRpcServer rpcServer = null;

    public RpcServlet() {
        super();
        rpcServer = new JsonRpcServer(new UserServiceImpl(), UserService.class);
    }

    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        rpcServer.handle(request, response);
    }
}