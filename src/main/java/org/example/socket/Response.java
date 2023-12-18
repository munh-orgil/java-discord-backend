package org.example.socket;

import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 2;
    public String message;
    public int status;
    public Object body;

    public Response(String message, int status, Object body) {
        this.message = message;
        this.status = status;
        this.body = body;
    }
}
