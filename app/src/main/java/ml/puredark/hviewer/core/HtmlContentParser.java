package ml.puredark.hviewer.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import ml.puredark.hviewer.utils.RegexValidateUtil;

/**
 * 网页内容解析
 *
 * Created by PureDark on 2016/10/23.
 */

public class HtmlContentParser {

    /**
     * 获取可点击的超文本标记
     *
     * @param context 上下文
     * @param html
     * @param sourceUrl
     * @param imageGetter
     * @return 字符序列
     */
    public static CharSequence getClickableHtml(Context context, String html, String sourceUrl, Html.ImageGetter imageGetter) {
        return getClickableHtml(context, html, sourceUrl, imageGetter, null);
    }

    /**
     *
     * @param context
     * @param html
     * @param sourceUrl
     * @param imageGetter
     * @param tagHandler
     * @return
     */
    public static CharSequence getClickableHtml(Context context, String html, String sourceUrl, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        Spanned spannedHtml = Html.fromHtml(html, imageGetter, tagHandler);
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for (final URLSpan span : urls) {
            setLinkClickable(context, clickableHtmlBuilder, span, sourceUrl);
        }

        return clickableHtmlBuilder;
    }

    /**
     *
     * @param context
     * @param clickableHtmlBuilder
     * @param urlSpan
     * @param sourceUrl
     */
    private static void setLinkClickable(Context context, final SpannableStringBuilder clickableHtmlBuilder,
                                         final URLSpan urlSpan, String sourceUrl) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                String url = urlSpan.getURL();
                url = TextUtils.isEmpty(url) ? "" : RegexValidateUtil.getAbsoluteUrlFromRelative(url, sourceUrl);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        };
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
    }
}
