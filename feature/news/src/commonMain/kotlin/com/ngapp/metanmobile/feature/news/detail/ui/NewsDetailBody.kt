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

package com.ngapp.metanmobile.feature.news.detail.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapp.metanmobile.core.designsystem.component.htmltext.HtmlText
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.ui.util.launchCustomChromeTab

@Composable
internal fun NewsDetailBody(content: String) {
    val backgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        HtmlText(
            text = content,
            onLinkClick = { url ->
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    launchCustomChromeTab(url, backgroundColor)
                }
            },
            style = MMTypography.headlineMedium,
            lineHeight = 22.sp,
            // The lead image is already shown by ItemDetailImageView above; drop the first <img>
            // in the body so it isn't repeated. Done inside the parser (rather than string-slicing
            // the raw HTML beforehand) so it still works when that <img> sits inside a wrapper tag
            // like "<b><img .../></b>" — a plain substringBefore("<img") lands mid-tag there and
            // fails to actually remove it.
            skipFirstImage = true,
        )
    }
}
