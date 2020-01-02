package com.nsg.nsgdtlibrary.Classes.util;

public class NavigationProperties {

    private String baseMap_Url_format;
    private String SourcePosition;
    private String DestinationPosition;
    private String routeData;
    private String entered_mode;
    private String deviation_bufferSize;
    private String routeDeviated_DT_URL;
    private String Authorisation_key="";

    public String getBaseMap_Url_format() {
        return baseMap_Url_format;
    }

    public void setBaseMap_Url_format(String baseMap_Url_format) {
        this.baseMap_Url_format = baseMap_Url_format;
    }

    public String getSourcePosition() {
        return SourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        SourcePosition = sourcePosition;
    }

    public String getDestinationPosition() {
        return DestinationPosition;
    }

    public void setDestinationPosition(String destinationPosition) {
        DestinationPosition = destinationPosition;
    }

    public String getRouteData() {
        return routeData;
    }

    public void setRouteData(String routeData) {
        this.routeData = routeData;
    }

    public String getEntered_mode() {
        return entered_mode;
    }

    public void setEntered_mode(String entered_mode) {
        this.entered_mode = entered_mode;
    }

    public String getDeviation_bufferSize() {
        return deviation_bufferSize;
    }

    public void setDeviation_bufferSize(String deviation_bufferSize) {
        this.deviation_bufferSize = deviation_bufferSize;
    }

    public String getRouteDeviated_DT_URL() {
        return routeDeviated_DT_URL;
    }

    public void setRouteDeviated_DT_URL(String routeDeviated_DT_URL) {
        this.routeDeviated_DT_URL = routeDeviated_DT_URL;
    }

    public String getAuthorisation_key() {
        return Authorisation_key;
    }

    public void setAuthorisation_key(String authorisation_key) {
        Authorisation_key = authorisation_key;
    }
}
