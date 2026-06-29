package com.sentinal.instance.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/instances")
public class InstanceController
{
    public Map<String, Object> test(Authentication authentication)
    {
        return  Map.of(
                "message", "INSTANCE SERVICE OK",
                "authenticated", authentication != null,
                "principal", authentication !=null ? authentication.getName() : "anonymous"
        );
    }
}
