package net.nightwhistler.htmlspanner.handlers;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.spans.*;
import net.nightwhistler.htmlspanner.style.Style;
import net.nightwhistler.htmlspanner.style.StyleCallback;
import org.htmlcleaner.TagNode;

/**
 * TagNodeHandler for any type of text that may be styled using CSS.
 *
 * @author Alex Kuiper
 */
public class StyledTextHandler extends TagNodeHandler {

    private Style style;

    public StyledTextHandler() {
        this.style = new Style();
    }

    public StyledTextHandler(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public void beforeChildren(TagNode node, SpannableStringBuilder builder, SpanStack spanStack) {
        Style styleFromCSS = spanStack.getStyle( node, getStyle() );

        if (styleFromCSS.getDisplayStyle() == Style.DisplayStyle.BLOCK &&
                builder.length() > 0
                && builder.charAt(builder.length() - 1) != '\n') {
            builder.append("\n");
        }
    }

    public final void handleTagNode(TagNode node, SpannableStringBuilder builder,
                                    int start, int end, SpanStack spanStack) {
        Style styleFromCSS = spanStack.getStyle( node, getStyle() );
        handleTagNode(node, builder, start, end, styleFromCSS, spanStack);
    }

    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, Style useStyle, SpanStack stack ) {

        if ( getStyle().getDisplayStyle() == Style.DisplayStyle.BLOCK ) {
            appendNewLine(builder);

            //If we have a bottom margin, we insert an extra newline. We'll manipulate the line height
            //of this newline to create the margin.
            if ( getStyle().getRelativeMarginBottom() != null && getStyle().getRelativeMarginBottom() > 0f ) {

                appendNewLine(builder);

                Log.d("StyledTextHandler", "Applying MarginSpan from style " + useStyle + " from " + (end -1) + " to "
                        + end + " on text " + builder.subSequence(end -1, end) );

                stack.pushSpan( new MarginSpan(useStyle.getRelativeMarginBottom() ),
                        builder.length() -1, builder.length() );

            }
        }

        stack.pushSpan(new StyleCallback(getSpanner().getFontResolver()
                .getDefaultFont(), useStyle, start, builder.length() ));
    }

}
