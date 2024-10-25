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

import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest

private const val URL_TAG = "url_tag"
private const val SUP_TAG = "sup_tag"

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    lineHeight: TextUnit = TextUnit.Unspecified,
    onLinkClick: ((String) -> Unit)? = null,
    fontSize: TextUnit = 14.sp,
    flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT,
    urlSpanStyle: SpanStyle = SpanStyle(
        color = linkTextColor(),
        textDecoration = TextDecoration.Underline
    ),
    imgSpanStyle: SpanStyle = SpanStyle(color = imgTextColor()),
    supSpanStyle: SpanStyle = SpanStyle(
        fontSize = 10.sp,
        baselineShift = BaselineShift.Superscript
    ),
) {
    val content = text.asHTML(fontSize, flags, urlSpanStyle, imgSpanStyle, supSpanStyle)
    if (onLinkClick != null) {
        ClickableText(
            modifier = modifier,
            text = content,
            style = style,
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
            onClick = {
                content
                    .getStringAnnotations(URL_TAG, it, it)
                    .firstOrNull()
                    ?.let { stringAnnotation -> onLinkClick(stringAnnotation.item) }
            }
        )
    } else {
        Text(
            modifier = modifier,
            text = content,
            style = style,
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
            lineHeight = lineHeight
        )
    }
}

@Composable
private fun linkTextColor() = Color(0xFF009CDE)

@Composable
private fun imgTextColor() = Color(0x00FFFFFF)

@Composable
private fun String.asHTML(
    fontSize: TextUnit,
    flags: Int,
    urlSpanStyle: SpanStyle,
    imgSpanStyle: SpanStyle,
    supSpanStyle: SpanStyle,
) = buildAnnotatedString {
    val trimmedText = this@asHTML.replace(Regex("\\s+"), " ").trim()
    val spanned = HtmlCompat.fromHtml(trimmedText, flags)
    val spans = spanned.getSpans(0, spanned.length, Any::class.java)

    append(spanned.toString())

    spans
        .filter { it !is BulletSpan }
        .forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is RelativeSizeSpan -> span.spanStyle(fontSize)
                is StyleSpan -> span.spanStyle()
                is UnderlineSpan -> span.spanStyle()
                is ForegroundColorSpan -> span.spanStyle()
                is TypefaceSpan -> span.spanStyle()
                is StrikethroughSpan -> span.spanStyle()
                is SuperscriptSpan -> {
                    addStringAnnotation(
                        tag = SUP_TAG,
                        annotation = "sup",
                        start = start,
                        end = end
                    )
                    supSpanStyle
                }

                is SubscriptSpan -> span.spanStyle()
                is URLSpan -> {
                    val url =
                        if (span.url.startsWith("https://metan.by") || span.url.startsWith("http://metan.by")) span.url else "https://metan.by${span.url}"
                    addStringAnnotation(
                        tag = URL_TAG,
                        annotation = url,
                        start = start,
                        end = end
                    )
                    urlSpanStyle
                }

                is ImageSpan -> {
                    val imageUrl: String? =
                        if (span.source?.startsWith("http://metan.by") == true ||
                            span.source?.startsWith("https://metan.by") == true ||
                            span.source?.startsWith("metan.by") == true
                        ) {
                            span.source
                        } else {
                            "https://metan.by${span.source}"
                        }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.Center)
                            .padding(vertical = 12.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentScale = ContentScale.Crop,
                            contentDescription = imageUrl,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.5f)
                        )
                    }
                    imgSpanStyle
                }

                else -> null
            }?.let { spanStyle ->
                addStyle(spanStyle, start, end)
            }
        }
}

