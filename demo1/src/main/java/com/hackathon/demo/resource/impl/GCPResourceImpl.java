package com.hackathon.demo.resource.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.demo.dto.Driver;
import com.hackathon.demo.dto.Location;
import com.hackathon.demo.dto.Order;
import com.hackathon.demo.dto.ResponseDto;
import com.hackathon.demo.resource.GCPResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;

@RestController
public class GCPResourceImpl implements GCPResource {

    @GetMapping("/order")
    public Response getOrderDetails(@RequestParam(value="orderId") String orderId) {
        try{
            File order = ResourceUtils.getFile("C:\\Users\\User\\Documents\\demo1\\src\\main\\resources\\order.json");
            File location = ResourceUtils.getFile("C:\\Users\\User\\Documents\\demo1\\src\\main\\resources\\location.json");
            File driver = ResourceUtils.getFile("C:\\Users\\User\\Documents\\demo1\\src\\main\\resources\\driver.json");

            InputStream inputStream_order = new FileInputStream(order);
            InputStream inputStream_location = new FileInputStream(location);
            InputStream inputStream_driver = new FileInputStream(driver);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode orderNode = mapper.readTree(inputStream_order).get("orders");
            JsonNode locationNode = mapper.readTree(inputStream_location).get("locations");
            JsonNode driverNode = mapper.readTree(inputStream_driver).get("drivers");

            List<Order> orders = mapper.convertValue(orderNode, new TypeReference<List<Order>>() {});
            List<Location> locations = mapper.convertValue(locationNode, new TypeReference<List<Location>>() {});
            List<Driver> drivers = mapper.convertValue(driverNode, new TypeReference<List<Driver>>() {});

            String message = null;

            for(Order order : orders){
                String locationValue = null;
                String driverContact = null;
                if(order.getId().equalsIgnoreCase(orderId)){
                    if("REL".equalsIgnoreCase(order.getStatus())){
                        message = "Order Number " + orderId + " is still in progress please check later";
                        break;
                    } else{
                        for(Location loc: locations){
                            if(loc.getId().equalsIgnoreCase(order.getLocationId())){
                                locationValue = loc.getLocation();
                            }
                        }

                        for (Driver dr:drivers){
                            if(dr.getId().equalsIgnoreCase(order.getDriverId())){
                                driverContact = dr.getContactNo();
                            }
                        }
                        message = "Order Number " +orderId + " is shipped and it's current location is " + locationValue +
                                ". Please get more information from our delivery person contacting " + driverContact + " number.";
                        break;
                    }
                } else {
                    message = "Sorry your order number is invalid please check";
                }
            }
            return Response.ok(new ResponseDto(message)).build();
        } catch (Exception e) {
            return Response.serverError().entity(new ResponseDto("Unexpected error occurred while processing please check later")).build();
        }
    }
}
