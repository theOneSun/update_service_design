package com.sun.temp.update_manyi.controller;

import com.sun.temp.update_manyi.service.CoreService;
import com.sun.temp.update_manyi.service.UpdateProjectDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunjian.
 */
@RestController
@Slf4j
public class MainController {
    private final UpdateProjectDataService service;
    private final CoreService coreService;


    @Autowired
    public MainController(UpdateProjectDataService service, CoreService coreService) {
        this.service = service;
        this.coreService = coreService;
    }

    @GetMapping("sql")
    public void sql() {
        final String sql = service.generateSql();
        log.info("sql:" + sql);
    }

    @GetMapping("update")
    public void update(){
        coreService.generateDate();
    }
}
