package com.sun.temp.update_manyi.controller;

import com.sun.temp.update_manyi.service.CoreService;
import com.sun.temp.update_manyi.service.DesignService;
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
    private final DesignService designService;


    @Autowired
    public MainController(UpdateProjectDataService service, CoreService coreService, DesignService designService) {
        this.service = service;
        this.coreService = coreService;
        this.designService = designService;
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

    @GetMapping("update_user_login")
    public void update_user_login(){
        designService.updateUserLogin();
    }

    @GetMapping("update_train")
    public void update_train(){
        designService.updateTrainProjectCreatedDate();
    }

    @GetMapping("replenish")
    public void replenish(){
        designService.modifyCreater();
    }

    @GetMapping("lack_project")
    public void lackProject(){
        designService.findNotInSystemProject();
    }
}
