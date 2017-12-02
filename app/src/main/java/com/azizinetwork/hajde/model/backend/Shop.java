package com.azizinetwork.hajde.model.backend;

public class Shop {

    private String objectId;
    private String shopDescription;
    private String shopTitle;
    private String submarkFilePath;
    private String markFilePath;
    private String country;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public String getSubmarkFilePath() {
        return submarkFilePath;
    }

    public void setSubmarkFilePath(String submarkFilePath) {
        this.submarkFilePath = submarkFilePath;
    }

    public String getMarkFilePath() {
        return markFilePath;
    }

    public void setMarkFilePath(String markFilePath) {
        this.markFilePath = markFilePath;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
