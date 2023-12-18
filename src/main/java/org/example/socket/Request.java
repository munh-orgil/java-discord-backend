package org.example.socket;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 99808453L;
    public String module;
    public String method;
    public String func;
    public Object body;
    public Request(String method, String module, String func, Object body) {
        this.module = module;
        this.method = method;
        this.func = func;
        this.body = body;
    }
    public Request(String method, String module, Object body) {
        this.module = module;
        this.method = method;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request: \n" +
                "module: " + module + "\n" +
                "method: " + method + "\n" +
                "body: " + body.toString() + "\n";
    }
}
