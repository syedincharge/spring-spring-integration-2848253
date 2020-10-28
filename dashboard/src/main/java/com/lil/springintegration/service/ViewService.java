package com.lil.springintegration.service;

import com.lil.springintegration.endpoint.TechSupportMessageHandler;
import com.lil.springintegration.manage.DashboardManager;
import com.lil.springintegration.util.AppSupportStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.channel.*;
import org.springframework.messaging.support.GenericMessage;

import java.util.Timer;
import java.util.TimerTask;

public class ViewService {

    static Logger logger = LoggerFactory.getLogger(DashboardManager.class);
    private Timer timer = new Timer();

    // TODO - refactor to use Spring Dependency Injection
    private AbstractSubscribableChannel statusMonitorChannel;
    private QueueChannel updateNotificationChannel;

    public ViewService() {
        updateNotificationChannel = (QueueChannel) DashboardManager.getDashboardContext().getBean("updateNotificationQueueChannel");
        statusMonitorChannel = (PublishSubscribeChannel) DashboardManager.getDashboardContext().getBean("statusMonitorChannel");
        statusMonitorChannel.subscribe(new ViewMessageHandler());
        this.start();
    }

    private void start() {
        /* Represents long-running process thread */
        timer.schedule(new TimerTask() {
            public void run() {
                checkForNotifications();
            }
        }, 3000, 3000);
    }

    private void checkForNotifications() {
        /* Check queue for notifications that the software needs to be updated */
        GenericMessage<?> message = (GenericMessage<?>) updateNotificationChannel.receive(1000);
        if (message != null) {
            AppSupportStatus payload = (AppSupportStatus) message.getPayload();
            DashboardManager.setDashboardStatus("softwareNotification", payload.getCustomerNotification());
        }
    }

    private static class ViewMessageHandler extends TechSupportMessageHandler {
        protected void receive(AppSupportStatus status) {
            DashboardManager.setDashboardStatus("softwareBuild", status.getRunningVersion());
        }
    }
}