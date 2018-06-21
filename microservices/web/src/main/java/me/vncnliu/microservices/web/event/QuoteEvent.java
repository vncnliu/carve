package me.vncnliu.microservices.web.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * add description<br>
 * created on 2018/2/13<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QuoteEvent {

    public QuoteEvent() {
    }

    public QuoteEvent(String code, double price) {
        this.code = code;
        this.price = price;
    }

    private String code;
    private double price;
    private long time;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static QuoteEvent buildByJson(String value) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        QuoteEvent quoteEvent = mapper.readValue(value,QuoteEvent.class);
        quoteEvent.setTime(System.currentTimeMillis());
        return quoteEvent;
    }

}
