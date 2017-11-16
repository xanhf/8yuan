package com.trade.eight.entity;

public class CalendarBean {

    private String id;
    private String eventRowId;
    private String timestamp;
    private String localDateTime;
    private String importance;
    private String title;
    private String forecast;
    private String actual;
    private String previous;
    private String category_id;
    private String relatedAssets;
    private String remark;
    private String mark;
    private String calendarType;
    private String country;
    private String currency;
    private String event_attr_id;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventRowId() {
        return eventRowId;
    }

    public void setEventRowId(String eventRowId) {
        this.eventRowId = eventRowId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getRelatedAssets() {
        return relatedAssets;
    }

    public void setRelatedAssets(String relatedAssets) {
        this.relatedAssets = relatedAssets;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEvent_attr_id() {
        return event_attr_id;
    }

    public void setEvent_attr_id(String event_attr_id) {
        this.event_attr_id = event_attr_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CalendarBean [id=" + id + ", eventRowId=" + eventRowId
                + ", timestamp=" + timestamp + ", localDateTime="
                + localDateTime + ", importance=" + importance + ", title="
                + title + ", forecast=" + forecast + ", actual=" + actual
                + ", previous=" + previous + ", category_id=" + category_id
                + ", relatedAssets=" + relatedAssets + ", remark=" + remark
                + ", mark=" + mark + ", calendarType=" + calendarType
                + ", country=" + country + ", currency=" + currency
                + ", event_attr_id=" + event_attr_id + ", description="
                + description + "]";
    }

}
