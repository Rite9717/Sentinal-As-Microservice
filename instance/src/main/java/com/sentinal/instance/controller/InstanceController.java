package com.sentinal.instance.controller;

import com.sentinal.instance.dto.IamRoleRequest;
import com.sentinal.instance.dto.MonitoringConfigRequest;
import com.sentinal.instance.dto.Request;
import com.sentinal.instance.dto.Response;
import com.sentinal.instance.service.InstanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instances")
public class InstanceController
{
    private final InstanceService instanceService;

    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @GetMapping("/test")
    public Map<String, Object> test(Principal principal)
    {
        return  Map.of(
                "service", "instance-service",
                "message", "Protected instance endpoint reached",
                "user", principal != null ? principal.getName() : "forwarded-by-gateway"
        );
    }

    @PostMapping
    public Response register(@RequestHeader("X-User-Id") Long userId, @RequestBody Request request)
    {
        return instanceService.registerInstance(userId, request);
    }

    @GetMapping
    public List<Response> list(@RequestHeader("X-User-Id") Long userId)
    {
        return instanceService.listInstances(userId);
    }

    @PostMapping("/{id}/iam/setup-started")
    public ResponseEntity<Response> startIamSetup (@PathVariable Long id, @RequestHeader("X-User-Id") Long userId)
    {
        return ResponseEntity.ok(instanceService.startIamSetup(id, userId));
    }

    @PostMapping("/{id}/iam/role")
    public ResponseEntity<Response> saveIamRole(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId, @RequestBody IamRoleRequest request)
    {
        return ResponseEntity.ok(instanceService.saveIamRole(id, userId, request));
    }

    @PostMapping("{id}/monitoring/config")
    public ResponseEntity<Response> saveMonitoringConfig(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId, @RequestBody MonitoringConfigRequest request)
    {
        return ResponseEntity.ok(instanceService.saveMonitoringConfig(id, userId, request));
    }

    @GetMapping("{id}/onboarding")
    public ResponseEntity<Response> getOnboarding(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId)
    {
        return ResponseEntity.ok(instanceService.getOnboarding(id, userId));
    }

    @PostMapping("/{id}/iam/verify")
    public ResponseEntity<Response> verifyIamRole(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId)
    {
        return ResponseEntity.ok(instanceService.verifyIamRole(id, userId));
    }

    @PostMapping("/{id}/monitoring/verify")
    public ResponseEntity<Response> verifyMonitoring(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId)
    {
        return ResponseEntity.ok(instanceService.verifyMonitoring(id, userId));
    }

    @GetMapping("/internal/ready-for-monitoring")
    public List<Response> listReadyForMonitoring()
    {
        return instanceService.listReadyForMonitoring();
    }

}
