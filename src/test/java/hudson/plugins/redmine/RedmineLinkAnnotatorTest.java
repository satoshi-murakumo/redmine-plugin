package hudson.plugins.redmine;

import hudson.MarkupText;
import hudson.plugins.redmine.RedmineLinkAnnotator;
import junit.framework.TestCase;

public class RedmineLinkAnnotatorTest extends TestCase {

    private static final String REDMINE_URL = "http://local.redmine/";

    public void testWikiLinkSyntax() {
        assertAnnotatedTextEquals("Nothing here.", "Nothing here.");
    }
    public void testWikiLinkSyntax1() {
        assertAnnotatedTextEquals("Text with WikiLink.", "Text with <a href='" + REDMINE_URL + "wiki/WikiLink'>WikiLink</a>.");
    }
    public void testWikiLinkSyntax2() {
        assertAnnotatedTextEquals("#42", "<a href='" + REDMINE_URL + "issues/show/42'>#42</a>");
    }
    public void testWikiLinkSyntax11() {
        assertAnnotatedTextEquals("refs #1,#11,#111,#1111",
                                  "refs <a href='" + REDMINE_URL + "issues/show/1'>#1</a>," +
                                    "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>," +
                                  "<a href='" + REDMINE_URL + "issues/show/111'>#111</a>," +
                                  "<a href='" + REDMINE_URL + "issues/show/1111'>#1111</a>");
    }
    public void testWikiLinkSyntax12() {
        assertAnnotatedTextEquals("refs #1, #11, #111,#1111",
                                  "refs <a href='" + REDMINE_URL + "issues/show/1'>#1</a>, " +
                                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>, " +
                                  "<a href='" + REDMINE_URL + "issues/show/111'>#111</a>," +
                                  "<a href='" + REDMINE_URL + "issues/show/1111'>#1111</a>");
    }
    public void testWikiLinkSyntax13() {
        assertAnnotatedTextEquals("refs #1",
                                  "refs <a href='" + REDMINE_URL + "issues/show/1'>#1</a>");
    }
    public void testWikiLinkSyntax14() {
        assertAnnotatedTextEquals("closes #1&amp;#11",
                                  "closes <a href='" + REDMINE_URL + "issues/show/1'>#1</a>&amp;" +
                                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>");
    }
    public void testWikiLinkSyntax15() {
        assertAnnotatedTextEquals("closes #1",
                                  "closes <a href='" + REDMINE_URL + "issues/show/1'>#1</a>");
    }
    public void testWikiLinkSyntax16() {
        assertAnnotatedTextEquals("IssueID #1 #11",
                                  "IssueID <a href='" + REDMINE_URL + "issues/show/1'>#1</a> " +
                                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a>");
    }
    public void testWikiLinkSyntax17() {
        assertAnnotatedTextEquals("IssueID #1 #11 sum problem.",
                                  "IssueID <a href='" + REDMINE_URL + "issues/show/1'>#1</a> " +
                                  "<a href='" + REDMINE_URL + "issues/show/11'>#11</a> sum problem.");
    }
    public void testWikiLinkSyntax18() {
        assertAnnotatedTextEquals("/abc/be#123", "/abc/be#123");
    }
    public void testWikiLinkSyntax19() {
        // unfortunately, this syntax not suported.
        //assertAnnotatedTextEquals("/abc/#123", "/abc/#123");
    }

    private void assertAnnotatedTextEquals(String originalText, String expectedAnnotatedText) {
        MarkupText markupText = new MarkupText(originalText);
        for (RedmineLinkAnnotator.LinkMarkup markup : RedmineLinkAnnotator.MARKUPS) {
            markup.process(markupText, REDMINE_URL);
        }
        assertEquals(expectedAnnotatedText, markupText.toString());
    }
}
