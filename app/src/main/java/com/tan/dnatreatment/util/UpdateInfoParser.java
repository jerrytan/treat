package com.tan.dnatreatment.util;

import android.util.Xml;

import com.tan.dnatreatment.dao.UpdateVersionInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by tanzhongyi on 2015/9/21.
 */
public class UpdateInfoParser {

    public static UpdateVersionInfo getUpdataInfo(InputStream is) throws Exception{
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");
        int type = parser.getEventType();
        UpdateVersionInfo info = new UpdateVersionInfo();
        while(type != XmlPullParser.END_DOCUMENT ){
            switch (type) {
                case XmlPullParser.START_TAG:
                    if("versionCode".equals(parser.getName())) {
                        info.setVersionCode(Integer.parseInt(parser.nextText()));
                    }else if ("apkUrl".equals(parser.getName())){
                        info.setApkUrl(parser.nextText());
                    }else if ("description".equals(parser.getName())){
                        info.setDescription(parser.nextText());
                    }else if ("note".equals(parser.getName())) {
                        info.setNote(parser.nextText());
                    }
                    break;
                }
            type = parser.next();
            }
        return info;
        }
}
