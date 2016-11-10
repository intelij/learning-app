package org.stepic.droid.util;

import android.net.Uri;
import android.util.Patterns;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepic.droid.base.MainApplication;
import org.stepic.droid.configuration.IConfig;
import org.stepic.droid.model.Lesson;
import org.stepic.droid.model.Section;
import org.stepic.droid.model.Step;
import org.stepic.droid.model.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class StringUtil {
    public static Double safetyParseString(String str) {
        Double doubleScore = null;
        try {
            doubleScore = Double.parseDouble(str);

        } catch (Exception ignored) {
        }
        return doubleScore;
    }

    public static String getUriForCourse(String baseUrl, String slug) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseUrl);
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append("course");
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append(slug);
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        return stringBuilder.toString();
    }

    public static String getUriForSyllabus(String baseUrl, String slug) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getUriForCourse(baseUrl, slug)).append(AppConstants.APP_INDEXING_SYLLABUS_MANIFEST);
        return stringBuilder.toString();
    }

    public static String getDynamicLinkForCourse(IConfig config, String slug) {
        String firebaseDomain = config.getFirebaseDomain();
        if (firebaseDomain == null) {
            return getUriForCourse(config.getBaseUrl(), slug);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firebaseDomain);
        stringBuilder.append("?amv=650");
        stringBuilder.append("&apn=");
        String packageName = MainApplication.getAppContext().getPackageName();
        if (packageName == null) {
            return getUriForCourse(config.getBaseUrl(), slug);
        }
        stringBuilder.append(packageName);


        stringBuilder.append("&link=");
        stringBuilder.append(config.getBaseUrl());
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append("course");
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append(HtmlHelper.parseIdFromSlug(slug));
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);

//        stringBuilder.append("&ibi=com.AlexKarpov.Stepic");
        return stringBuilder.toString();
    }

//    private static final Pattern urlPattern = Pattern.compile(
//            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
//                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
//                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
//            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


    //Pull all links from the body for easy retrieval
    public static List<String> pullLinks(String fromHtmlText) {
        List<String> links = new ArrayList<>();

        Matcher m = Patterns.WEB_URL.matcher(fromHtmlText);
        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            urlStr = urlStr.trim();
            links.add(urlStr);
        }
        return links;
    }

    public static Uri getAppUriForCourse(String baseUrl, String slug) {
        StringBuilder stringBuilder = getAppUriStringBuilderForCourse(baseUrl, slug).append(AppConstants.APP_INDEXING_COURSE_DETAIL_MANIFEST_HACK);
        return Uri.parse(stringBuilder.toString());
    }

    public static Uri getAppUriForCourseSyllabus(String baseUrl, String slug) {
        StringBuilder stringBuilder = getAppUriStringBuilderForCourse(baseUrl, slug).append(AppConstants.APP_INDEXING_SYLLABUS_MANIFEST);
        return Uri.parse(stringBuilder.toString());
    }

    private static StringBuilder getAppUriStringBuilderForCourse(String baseUrl, String slug) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android-app://");
        stringBuilder.append(DeviceInfoUtil.getPackageName());
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append("https");
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        String host = Uri.parse(baseUrl).getHost();
        stringBuilder.append(host);
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append("course");
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        stringBuilder.append(slug);
        stringBuilder.append(AppConstants.WEB_URI_SEPARATOR);
        return stringBuilder;
    }

    public static String getAbsoluteUriForSection(IConfig config, @NotNull Section section) {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(config.getBaseUrl());
        sb.append(AppConstants.WEB_URI_SEPARATOR);
        sb.append("course");
        sb.append(AppConstants.WEB_URI_SEPARATOR);
        sb.append(section.getCourse());
        sb.append(AppConstants.WEB_URI_SEPARATOR);
        sb.append("syllabus");
        sb.append("?module=");
        sb.append(section.getPosition());
        return sb.toString();
    }

    public static String getUriForStep(@NotNull String baseUrl, @NotNull Lesson lesson, @Nullable Unit unit, @NotNull Step step) {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append(AppConstants.WEB_URI_SEPARATOR);
        sb.append("lesson");
        sb.append(AppConstants.WEB_URI_SEPARATOR);
        if (lesson.getSlug() != null && !lesson.getSlug().isEmpty()) {
            sb.append(lesson.getSlug());
        } else {
            sb.append(lesson.getId());
        }
        sb.append(AppConstants.WEB_URI_SEPARATOR);

        sb.append("step");
        sb.append(AppConstants.WEB_URI_SEPARATOR);
        sb.append(step.getPosition());
        if (unit != null) {
            sb.append("?unit=");
            sb.append(unit.getId());
        }
        return sb.toString();
    }
}
