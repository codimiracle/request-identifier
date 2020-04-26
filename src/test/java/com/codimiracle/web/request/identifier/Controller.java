package com.codimiracle.web.request.identifier;

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
import com.codimiracle.web.request.identifier.enumeration.IdentifierStrategy;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @NonRepeatable
    public String onlyOnce(String test) {
        return "Your submission is accepted.";
    }

    @NonRepeatable(interval = NonRepeatable.DEFAULT_INTERVAL)
    public String interval(String user, boolean like) {
        return "OK";
    }

    @NonRepeatable(interval = 2000)
    public String intervalCustom(String user, String msg) {
        return "Submitted";
    }

    @NonRepeatable
    public String customArg(CustomArg arg) {
        return "ok";
    }

    @GetMapping("/hello")
    @NonRepeatable(strategy = IdentifierStrategy.REQUEST_PARAMETER, parameterName = "request_id")
    public String byParameter(CustomArg arg) {
        return "Accepted";
    }

    @GetMapping("/hi")
    @NonRepeatable(strategy = IdentifierStrategy.REQUEST_PARAMETER)
    public String byParameterAll(CustomArg arg) {
        return "Accepted";
    }


    @Data
    public static class CustomArg {
        private String name;
        private String status;
        private long age;
    }
}
