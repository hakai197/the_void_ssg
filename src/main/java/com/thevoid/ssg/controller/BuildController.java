package com.thevoid.ssg.controller;

import com.thevoid.ssg.model.dto.BuildLogDto;
import com.thevoid.ssg.model.dto.BuildResultDto;
import com.thevoid.ssg.service.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/sites/{siteId}/builds")
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;

    @PostMapping
    public CompletableFuture<ResponseEntity<BuildResultDto>> triggerBuild(@PathVariable String siteId) {
        return buildService.buildSiteAsync(siteId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping
    public ResponseEntity<List<BuildLogDto>> getBuildHistory(@PathVariable String siteId) {
        List<BuildLogDto> history = buildService.getBuildHistory(siteId);
        return ResponseEntity.ok(history);
    }
}