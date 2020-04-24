package com.codimiracle.web.request.identifier;

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
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
}
