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

package com.ngapp.metanmobile.lint.designsystem

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * A detector that checks for incorrect usages of Compose Material APIs over equivalents in
 * the Shot in time design system module.
 */
class DesignSystemDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(
            UCallExpression::class.java,
            UQualifiedReferenceExpression::class.java,
        )
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return
                val preferredName = METHOD_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name = node.receiver.asRenderString()
                val preferredName = RECEIVER_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DesignSystem",
            briefDescription = "Design system",
            explanation = "This check highlights calls in code that use Compose Material " +
                "composables instead of equivalents from the Shot in time design system " +
                "module.",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                DesignSystemDetector::class.java,
                Scope.JAVA_FILE_SCOPE,
            ),
        )

        // NOTE: Unfortunately :lint is a Java module and thus can't depend on the :core-designsystem
        //  Android module, so we can't use composable function references (eg. ::Button.name)
        //  instead of hardcoded names.
        val METHOD_NAMES = mapOf(
            "MaterialTheme" to "MMTheme",
            "Button" to "MMButton",
            "OutlinedButton" to "MMOutlinedButton",
            "TextButton" to "MMTextButton",
            "FilterChip" to "MMFilterChip",
            "ElevatedFilterChip" to "MMFilterChip",
            "NavigationBar" to "MMNavigationBar",
            "NavigationBarItem" to "MMNavigationBarItem",
            "NavigationRail" to "MMNavigationRail",
            "NavigationRailItem" to "MMNavigationRailItem",
            "TabRow" to "MMTabRow",
            "Tab" to "MMTab",
            "IconToggleButton" to "MMIconToggleButton",
            "FilledIconToggleButton" to "MMIconToggleButton",
            "FilledTonalIconToggleButton" to "MMIconToggleButton",
            "OutlinedIconToggleButton" to "MMIconToggleButton",
            "CenterAlignedTopAppBar" to "MMTopAppBar",
            "Switch" to "MMSwitch",
            "SmallTopAppBar" to "MMTopAppBar",
            "MediumTopAppBar" to "MMTopAppBar",
            "LargeTopAppBar" to "MMTopAppBar",
        )
        val RECEIVER_NAMES = mapOf(
            "Icons" to "MMIcons",
        )

        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using $name instead of $preferredName",
            )
        }
    }
}
