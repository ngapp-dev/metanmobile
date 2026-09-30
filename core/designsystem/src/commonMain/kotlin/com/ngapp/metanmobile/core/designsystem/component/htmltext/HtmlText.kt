/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ngapp.metanmobile.core.designsystem.component.htmltext

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter

private const val URL_TAG = "url_tag"
private val VOID_TAGS = setOf("br", "img", "hr", "input", "meta", "link")
private val BLOCK_TAGS = setOf("p", "div", "li", "ul", "ol", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote")
private const val BASE_URL = "https://metan.by"

/**
 * Minimal, multiplatform HTML renderer for the small subset of markup that comes back from the
 * Metan Mobile feed/news content: paragraphs, bold/italic/underline/strikethrough, links,
 * superscript and inline images. Unknown tags are unwrapped (their text content is kept).
 */
@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    urlSpanStyle: SpanStyle = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
    onLinkClick: ((String) -> Unit)? = null,
    // News content repeats its lead image as the very first <img> in the body — the same picture
    // already shown above by ItemDetailImageView — so callers showing that hero separately pass
    // true here to drop just that first <img>, wherever it is in the markup, instead of every
    // caller hand-rolling its own (easily broken) string surgery to strip it beforehand.
    skipFirstImage: Boolean = false,
) {
    val blocks = remember(text, skipFirstImage) { parseHtml(text, skipFirstImage) }
    // Mirror Text()'s own color-resolution order (explicit param -> style -> ambient content
    // color) instead of always collapsing to LocalContentColor: MMTypography styles (e.g.
    // headlineMedium) already bake in a theme-correct Black/White color, and short-circuiting
    // past that here was making body text render in whatever color happens to be ambient.
    val resolvedColor = when {
        color != Color.Unspecified -> color
        style.color != Color.Unspecified -> style.color
        else -> LocalContentColor.current
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is HtmlBlock.Paragraph -> {
                    val annotated = block.build(urlSpanStyle)
                    if (onLinkClick != null) {
                        ClickableText(
                            text = annotated,
                            style = style.copy(color = resolvedColor, fontSize = fontSize, lineHeight = lineHeight),
                            maxLines = maxLines,
                            overflow = overflow,
                            onClick = { offset ->
                                annotated.getStringAnnotations(URL_TAG, offset, offset)
                                    .firstOrNull()
                                    ?.let { onLinkClick(it.item) }
                            },
                        )
                    } else {
                        Text(
                            text = annotated,
                            style = style,
                            color = resolvedColor,
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            maxLines = maxLines,
                            overflow = overflow,
                        )
                    }
                }

                is HtmlBlock.Image -> {
                    val painter = rememberAsyncImagePainter(model = block.url)
                    val state = painter.state
                    // Reserve the normal 1.5:1 box while loading (or once it succeeds) so
                    // surrounding text doesn't jump around as the image resolves, but collapse to
                    // nothing on failure instead of leaving a large blank hole where a broken
                    // inline image (dead link, unreachable host) would otherwise sit.
                    if (state !is AsyncImagePainter.State.Error) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(align = Alignment.Center),
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = block.url,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.5f),
                            )
                        }
                    }
                }
            }
        }
    }
}

private val linkColor = Color(0xFF009CDE)

private sealed interface HtmlBlock {
    data class Paragraph(val runs: List<Run>) : HtmlBlock {
        fun build(urlSpanStyle: SpanStyle): AnnotatedString = buildAnnotatedString {
            runs.forEach { run ->
                val start = length
                append(run.text)
                run.styles.forEach { addStyle(it, start, length) }
                if (run.href != null) {
                    addStyle(urlSpanStyle, start, length)
                    addStringAnnotation(URL_TAG, resolveUrl(run.href), start, length)
                }
            }
        }
    }

    data class Image(val url: String) : HtmlBlock
}

private data class Run(val text: String, val styles: List<SpanStyle>, val href: String? = null)

private fun resolveUrl(url: String): String = when {
    url.startsWith("http://") || url.startsWith("https://") -> url
    url.startsWith("$BASE_URL/") -> url
    else -> "$BASE_URL$url"
}

/** Very small streaming HTML tokenizer/parser, enough for feed-style markup. */
private fun parseHtml(html: String, skipFirstImage: Boolean = false): List<HtmlBlock> {
    val normalized = html.replace(Regex("\\s+"), " ").trim()
    val blocks = mutableListOf<HtmlBlock>()
    val currentRuns = mutableListOf<Run>()
    val styleStack = mutableListOf<SpanStyle>()
    val hrefStack = mutableListOf<String>()
    var pendingSkipImage = skipFirstImage

    // Adjacent <p>/<br> boundaries are collapsed into a *pending* line-break run instead of each
    // ending its own Paragraph block/Text composable straight away. android.text.Html (what
    // master renders through) folds a whole article into one Spanned string with embedded "\n"s,
    // so paragraph gaps are just blank text lines; splitting every <p> into its own Text here
    // instead stacked each composable's own line-height leading on top of Column's spacing,
    // roughly doubling the visual gap. Keeping same-paragraph-group runs in one block (and one
    // Text) reproduces the tighter, single-Spanned look. A block boundary requests "\n\n" (one
    // blank line, like a real paragraph break); <br> requests "\n" — two <br> in a row therefore
    // still line up into one blank line, same as the source markup intends.
    var pendingSeparator = ""

    fun requestSeparator(separator: String) {
        if (currentRuns.isEmpty()) return // nothing to separate yet (leading/empty block)
        if (separator.length > pendingSeparator.length) pendingSeparator = separator
    }

    // keepTrailingSeparator is true only for the very last flush (end of the whole string): a
    // source ending in a block tag (e.g. "...text</p>") requests a "\n\n" separator that would
    // normally only become a Run once more text follows. android.text.Html — what master renders
    // through — bakes that same trailing blank line into its Spanned output too (Html.fromHtml
    // doesn't trim it), which is what gives master's FAQ/careers/etc. answer text its bit of
    // bottom breathing room before the next row's divider; dropping it here (the old unconditional
    // "pendingSeparator = ''") was quietly losing that space in this from-scratch parser.
    fun flushParagraph(keepTrailingSeparator: Boolean = false) {
        if (currentRuns.isNotEmpty()) {
            if (keepTrailingSeparator && pendingSeparator.isNotEmpty()) {
                currentRuns += Run(pendingSeparator, emptyList())
            }
            blocks += HtmlBlock.Paragraph(currentRuns.toList())
            currentRuns.clear()
        }
        pendingSeparator = ""
    }

    fun appendText(raw: String) {
        val decoded = decodeEntities(raw)
        if (decoded.isEmpty()) return
        // True at the first real (non-whitespace) text since a paragraph boundary — the CMS
        // source indents its markup ("<p>\n\t ОАО..."), and after whitespace-collapsing that
        // indentation survives as one leading space on the paragraph's first text node. Android's
        // Html parser trims that (block-leading whitespace is insignificant in HTML), so we need
        // to as well, or every paragraph starts one space further in than the original.
        val startsNewBlock = currentRuns.isEmpty() || pendingSeparator.isNotEmpty()
        if (decoded.isBlank()) {
            // A text node that's nothing but whitespace is usually just source formatting: the
            // "\n\t " sitting between "<p>" and "</p>" of a truly empty paragraph (a common
            // CMS-blank-line artifact — see the raw feed content), or between a block tag and the
            // next one. If a separator is already pending, this whitespace IS that gap — drop it
            // rather than let it flush the pending "\n\n" as a stray space and then re-request a
            // second one, which was doubling every blank-<p> gap into two blank lines. Only when
            // there's no pending boundary and we're mid-paragraph (e.g. "</b> <i>") does the
            // whitespace matter, as an inline word separator — keep it verbatim then.
            if (startsNewBlock) return
            currentRuns += Run(decoded, styleStack.toList(), hrefStack.lastOrNull())
            return
        }
        if (pendingSeparator.isNotEmpty()) {
            currentRuns += Run(pendingSeparator, emptyList())
            pendingSeparator = ""
        }
        val text = if (startsNewBlock) decoded.trimStart() else decoded
        currentRuns += Run(text, styleStack.toList(), hrefStack.lastOrNull())
    }

    var i = 0
    val n = normalized.length
    while (i < n) {
        val lt = normalized.indexOf('<', i)
        if (lt < 0) {
            appendText(normalized.substring(i))
            break
        }
        if (lt > i) appendText(normalized.substring(i, lt))
        val gt = normalized.indexOf('>', lt)
        if (gt < 0) break
        val rawTag = normalized.substring(lt + 1, gt).trim()
        i = gt + 1

        if (rawTag.isEmpty()) continue
        val closing = rawTag.startsWith("/")
        val selfClosing = rawTag.endsWith("/")
        val tagContent = rawTag.removePrefix("/").removeSuffix("/").trim()
        val spaceIdx = tagContent.indexOfFirst { it.isWhitespace() }
        val tagName = (if (spaceIdx >= 0) tagContent.substring(0, spaceIdx) else tagContent).lowercase()
        val attrsPart = if (spaceIdx >= 0) tagContent.substring(spaceIdx + 1) else ""

        if (closing) {
            when (tagName) {
                "b", "strong" -> styleStack.removeLastStyleOrNull { it.fontWeight != null }
                "i", "em" -> styleStack.removeLastStyleOrNull { it.fontStyle != null }
                "u" -> styleStack.removeLastStyleOrNull { it.textDecoration == TextDecoration.Underline }
                "s", "strike", "del" -> styleStack.removeLastStyleOrNull { it.textDecoration == TextDecoration.LineThrough }
                "sup" -> styleStack.removeLastStyleOrNull { it.baselineShift == BaselineShift.Superscript }
                "sub" -> styleStack.removeLastStyleOrNull { it.baselineShift == BaselineShift.Subscript }
                "a" -> if (hrefStack.isNotEmpty()) hrefStack.removeAt(hrefStack.lastIndex)
                in BLOCK_TAGS -> requestSeparator("\n\n")
            }
            continue
        }

        when (tagName) {
            "br" -> requestSeparator("\n")
            "img" -> {
                val src = extractAttr(attrsPart, "src")
                if (src != null) {
                    if (pendingSkipImage) {
                        pendingSkipImage = false
                    } else {
                        flushParagraph()
                        blocks += HtmlBlock.Image(resolveUrl(src))
                    }
                }
            }

            "b", "strong" -> styleStack += SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            "i", "em" -> styleStack += SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            "u" -> styleStack += SpanStyle(textDecoration = TextDecoration.Underline)
            "s", "strike", "del" -> styleStack += SpanStyle(textDecoration = TextDecoration.LineThrough)
            "sup" -> styleStack += SpanStyle(fontSize = 10.sp, baselineShift = BaselineShift.Superscript)
            "sub" -> styleStack += SpanStyle(fontSize = 10.sp, baselineShift = BaselineShift.Subscript)
            "a" -> hrefStack += extractAttr(attrsPart, "href").orEmpty()
            in BLOCK_TAGS -> requestSeparator("\n\n")
            else -> Unit // unknown/inline tag: keep text content, ignore styling
        }
        if (selfClosing && tagName !in VOID_TAGS) {
            // e.g. <br/> already handled above; nothing else needed for generic self-closing tags
        }
    }
    flushParagraph(keepTrailingSeparator = true)
    return blocks
}

private inline fun MutableList<SpanStyle>.removeLastStyleOrNull(predicate: (SpanStyle) -> Boolean) {
    val idx = indexOfLast(predicate)
    if (idx >= 0) removeAt(idx)
}

private fun extractAttr(attrs: String, name: String): String? {
    val regex = Regex("""$name\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
    val single = Regex("""$name\s*=\s*'([^']*)'""", RegexOption.IGNORE_CASE)
    return regex.find(attrs)?.groupValues?.get(1) ?: single.find(attrs)?.groupValues?.get(1)
}

private val NUMERIC_ENTITY = Regex("""&#(x[0-9a-fA-F]+|\d+);""")

private fun decodeEntities(text: String): String = text
    .replace("&nbsp;", " ")
    .replace("&lt;", "<")
    .replace("&gt;", ">")
    .replace("&quot;", "\"")
    .replace("&apos;", "'")
    .replace("&mdash;", "—")
    .replace("&ndash;", "–")
    .replace("&hellip;", "…")
    .replace("&laquo;", "«")
    .replace("&raquo;", "»")
    .replace(NUMERIC_ENTITY) { match ->
        val ref = match.groupValues[1]
        val codePoint = if (ref.startsWith("x", ignoreCase = true)) {
            ref.substring(1).toIntOrNull(16)
        } else {
            ref.toIntOrNull()
        }
        codePoint?.let { codePointToString(it) } ?: match.value
    }
    // &amp; must be decoded last, so an entity like &amp;nbsp; (a double-escaped &nbsp;) doesn't
    // get corrupted by the other replacements running first.
    .replace("&amp;", "&")

/** kotlin.Char is UTF-16, so code points above the BMP need a manual surrogate pair — there's no
 * java.lang.Character to fall back on in commonMain. */
private fun codePointToString(codePoint: Int): String = if (codePoint <= 0xFFFF) {
    codePoint.toChar().toString()
} else {
    val c = codePoint - 0x10000
    val high = (c shr 10) + 0xD800
    val low = (c and 0x3FF) + 0xDC00
    charArrayOf(high.toChar(), low.toChar()).concatToString()
}
