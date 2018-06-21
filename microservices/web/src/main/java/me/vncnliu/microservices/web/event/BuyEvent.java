package me.vncnliu.microservices.web.event;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * add description<br>
 * created on 2018/2/13<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class BuyEvent {

    private int uid;
    private DeferredResult<String> result;

    public BuyEvent(int uid, DeferredResult<String> result) {
        this.uid = uid;
        this.result = result;
    }

    public int getUid() {
        return uid;
    }

    public DeferredResult<String> getResult() {
        return result;
    }
}
