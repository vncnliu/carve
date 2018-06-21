package me.vncnliu.microservices.web.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import reactor.core.publisher.FluxSink;

/**
 * add description<br>
 * created on 2018/2/13<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QuoteEventListener {

    private FluxSink<String> fluxSink;

    public QuoteEventListener(FluxSink<String> fluxSink) {
        this.fluxSink = fluxSink;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void execute(QuoteEvent quoteEvent){
        ObjectMapper mapper = new ObjectMapper();
        try {
            fluxSink.next(mapper.writeValueAsString(quoteEvent));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
