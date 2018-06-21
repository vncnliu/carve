package top.vncnliu.carve.java.kafka;

import java.util.Map;

/**
 * add description<br>
 * created on 2017/12/21<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class Quotes {
    private long priceTime;
    private int symbolUid;
    private Map<Short,Double> itemList;

    public void setPriceTime(long priceTime) {
        this.priceTime = priceTime;
    }

    public void setSymbolUid(int symbolUid) {
        this.symbolUid = symbolUid;
    }

    public long getPriceTime() {
        return priceTime;
    }

    public int getSymbolUid() {
        return symbolUid;
    }

    public Map<Short, Double> getItemList() {
        return itemList;
    }

    public void setItemList(Map<Short, Double> itemList) {
        this.itemList = itemList;
    }
}
