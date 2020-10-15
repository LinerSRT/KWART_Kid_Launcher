package com.sgtc.launcher.ClockSkin.Model;

import android.graphics.Bitmap;

import java.io.File;

public class ClockSkin {
    private final Bitmap previewImage;
    private final String clockPath;
    private final String previewPath;
    private final String manifestPath;
    private final boolean isExternalStorage;
    private final boolean isDownloadButton;
    private final boolean isLinerSkin;


    public static class Builder {
        private Bitmap previewImage = null;
        private String clockPath = "ClockSkin/09";
        private String previewPath = clockPath+ File.separator+"img_clock_preview.png";
        private String manifestPath = clockPath+ File.separator+"clock_skin.xml";
        private boolean isExternalStorage = false;
        private boolean isDownloadButton = false;
        private boolean isLinerSkin = false;


        public Builder() {
        }

        public Builder previewImage(Bitmap image){
            previewImage = image;
            return this;
        }
        public Builder clockPath(String path){
            clockPath = path;
            return this;
        }
        public Builder previewPath(String path){
            previewPath = path;
            return this;
        }
        public Builder manifestPath(String path){
            manifestPath = path;
            return this;
        }
        public Builder isExternalStorage(boolean value){
            isExternalStorage = value;
            return this;
        }
        public Builder isDownloadButton(boolean value){
            isDownloadButton = value;
            return this;
        }

        public Builder isLinerSkin(boolean value) {
            isLinerSkin = value;
            return this;
        }

        public ClockSkin build() {
            return new ClockSkin(this);
        }
    }



    private ClockSkin(Builder builder) {
        this.previewImage = builder.previewImage;
        this.clockPath = builder.clockPath;
        this.previewPath = builder.previewPath;
        this.manifestPath = builder.manifestPath;
        this.isExternalStorage = builder.isExternalStorage;
        this.isDownloadButton = builder.isDownloadButton;
        this.isLinerSkin = builder.isLinerSkin;
    }

    public String getClockPath() {
        return clockPath;
    }

    public Bitmap getPreviewImage() {
        return previewImage;
    }

    public String getManifestPath() {
        return manifestPath;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public boolean isDownloadButton() {
        return isDownloadButton;
    }

    public boolean isExternalStorage() {
        return isExternalStorage;
    }

    public boolean isLinerSkin() {
        return isLinerSkin;
    }

    @Override
    public String toString() {
        return "\nClockSkin{" +
                "\nclockPath='" + clockPath + '\'' +
                ",\n previewPath='" + previewPath + '\'' +
                ",\n manifestPath='" + manifestPath + '\'' +
                ",\n isExternalStorage=" + isExternalStorage +
                ",\n isDownloadButton=" + isDownloadButton +
                ",\n isLinerSkin=" + isLinerSkin +
                '}';
    }
}
