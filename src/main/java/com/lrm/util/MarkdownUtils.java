package com.lrm.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.*;

/**
 * Author: maxine yang
 */
public class MarkdownUtils {

    /**
     * Convert markdown format to HTML format
     * @param markdown
     * @return
     */
    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * Add extensions [heading anchors, table generation]
     * Convert Markdown to HTML
     * @param markdown
     * @return
     */
    public static String markdownToHtmlExtensions(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        // Generate id for h headings
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        // Convert table to HTML
        List<Extension> tableExtension = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    public AttributeProvider create(AttributeProviderContext context) {
                        return new CustomAttributeProvider();
                    }
                })
                .build();
        return renderer.render(document);
    }

    /**
     * Handle tag attributes
     */
    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            // Change a tag's target attribute to _blank
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }


    /**
     * Extract first image URL from markdown content
     * @param markdown Markdown content
     * @return First image URL found, or null if no image found
     */
    public static String extractFirstImage(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return null;
        }
        
        // Pattern 1: ![alt](url)
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
        java.util.regex.Matcher matcher1 = pattern1.matcher(markdown);
        if (matcher1.find()) {
            String url = matcher1.group(1).trim();
            if (!url.isEmpty() && (url.startsWith("http") || url.startsWith("/"))) {
                return url;
            }
        }
        
        // Pattern 2: <img src="url">
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher2 = pattern2.matcher(markdown);
        if (matcher2.find()) {
            String url = matcher2.group(1).trim();
            if (!url.isEmpty() && (url.startsWith("http") || url.startsWith("/"))) {
                return url;
            }
        }
        
        // Pattern 3: Direct image URL (http:// or https://)
        java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile("(https?://[^\\s)]+\\.(jpg|jpeg|png|gif|webp|bmp))", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher3 = pattern3.matcher(markdown);
        if (matcher3.find()) {
            return matcher3.group(1);
        }
        
        return null;
    }

    public static void main(String[] args) {
        String table = "| hello | hi   | 哈哈哈   |\n" +
                "| ----- | ---- | ----- |\n" +
                "| 斯维尔多  | 士大夫  | f啊    |\n" +
                "| 阿什顿发  | 非固定杆 | 撒阿什顿发 |\n" +
                "\n";
        String a = "[maxine's blog](https://github.com/maxine-yang)";
        System.out.println(markdownToHtmlExtensions(a));
    }
}
