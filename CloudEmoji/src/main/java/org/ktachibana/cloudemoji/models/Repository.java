package org.ktachibana.cloudemoji.models;

import android.content.Context;

import com.orm.SugarRecord;

import org.apache.commons.io.FilenameUtils;
import org.ktachibana.cloudemoji.Constants;

import java.io.Serializable;

/**
 * POJO class holding a remote repository and its relevant information
 */
public class Repository extends SugarRecord<Repository> implements Constants, Serializable {
    private String url;
    private String alias;
    private String fileName;
    private boolean isAvailable;
    private boolean isVisible;
    private int formatType;

    public Repository(Context context) {
        super(context);
    }

    public Repository(Context context, String url, String alias) {
        super(context);
        this.url = url;
        this.alias = alias;
        this.fileName = FilenameUtils.getName(url);
        this.isAvailable = false;
        this.isVisible = false;
        String extension = FilenameUtils.getExtension(url);
        if (extension.equals("xml")) formatType = FORMAT_TYPE_XML;
        if (extension.equals("json")) formatType = FORMAT_TYPE_JSON;
    }

    public String getUrl() {
        return url;
    }

    public String getAlias() {
        return alias;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public FormatType getFormatType() {
        if (formatType == FORMAT_TYPE_XML) return FormatType.XML;
        else return FormatType.JSON;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public static enum FormatType {XML, JSON}
}