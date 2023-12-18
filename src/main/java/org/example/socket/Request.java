package org.example.socket;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 99808453L;
    public String module;
    public String method;
    public int pageNumber;
    public int pageSize;
    public String type;
    public Object body;

    public Request(String method, String module, int pageNumber, int pageSize, String type, Object body) {
        this.module = module;
        this.method = method;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.type = type;
        this.body = body;
    }
    public Request(String method, String module, int pageNumber, int pageSize, Object body) {
        this.module = module;
        this.method = method;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.body = body;
    }
    public Request(String method, String module, String type, Object body) {
        this.module = module;
        this.method = method;
        this.type = type;
        this.body = body;
    }
    public Request(String method, String module, Object body) {
        this.module = module;
        this.method = method;
        this.body = body;
    }
}
