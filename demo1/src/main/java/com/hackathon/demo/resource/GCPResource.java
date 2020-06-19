package com.hackathon.demo.resource;

import javax.ws.rs.core.Response;

public interface GCPResource {
    Response getOrderDetails(String orderId);
}
