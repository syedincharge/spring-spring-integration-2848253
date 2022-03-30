package com.lil.springintegration.service;

import com.lil.springintegration.domain.AppSupportStatus;
import com.lil.springintegration.endpoint.TechSupportMessageFilter;
import com.lil.springintegration.endpoint.TechSupportMessageHandler;
import com.lil.springintegration.manage.DashboardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;

import java.util.Timer;

public class StatusMonitorService {

    static Logger logger = LoggerFactory.getLogger(DashboardManager.class);
    private Timer timer = new Timer();

    private AppSupportStatus currentLocalStatus;

    // TODO - refactor to use Spring Dependency Injection
    private AbstractSubscribableChannel statusMonitorChannel;
    private DirectChannel apiInputChannel;

    public StatusMonitorService() {
        apiInputChannel = (DirectChannel) DashboardManager.getDashboardContext().getBean("apiInputChannel");
        statusMonitorChannel = (PublishSubscribeChannel) DashboardManager.getDashboardContext().getBean("statusMonitorChannel");
        statusMonitorChannel.subscribe(new ServiceMessageHandler());
    }

    public static class ServiceMessageFilter extends TechSupportMessageFilter {
        protected boolean filterMessage(AppSupportStatus status) {
            return status.isUpdateRequired() || status.isDeviceOut();
        }
    }

    private class ServiceMessageHandler extends TechSupportMessageHandler {

        protected void receive(AppSupportStatus status) {
            setCurrentSupportStatus(status);
        }
    }

         private void setCurrentSupportStatus(AppSupportStatus status){
            this.currentLocalStatus = status;
        }


}
