package com.zapphireye.zapphireye.model.controller;

import com.zapphireye.zapphireye.helper.scheduler.ScheduleScanTask;
import com.zapphireye.zapphireye.model.database.Scan;
import com.zapphireye.zapphireye.model.request.CreateScanRequest;
import com.zapphireye.zapphireye.service.ScanService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/scan")
@AllArgsConstructor
public class ScanController {

    private ScanService scanService;
    private TaskScheduler taskScheduler;


    @GetMapping
    public List<Scan> fetchAllScans(){
        return scanService.findAll();
    }

    @PostMapping(value = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String startScan(@RequestBody(required = true) CreateScanRequest request){
        int periodDays = 3;

        switch (request.getPeriod()) {
            case "Monthly":
                periodDays = 30;
                break;
            case "3Weeks":
                periodDays = 21;
                break;
            case "2Weeks":
                periodDays = 14;
                break;
            case "Weekly":
                periodDays = 7;
                break;
            case "3Months":
                periodDays = 90;
                break;
            case "Yearly":
                periodDays = 365;
                break;
            case "3Minutes":
                periodDays = 3;
                break;
        }

        taskScheduler.schedule(new ScheduleScanTask(request, scanService), new PeriodicTrigger(periodDays, TimeUnit.MINUTES));
        return "OK";
    }


}