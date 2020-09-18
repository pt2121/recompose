/*
 * Copyright 2020 Sebastian Kaspari
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
 */

package recompose.composer.writer

import recompose.ast.ViewNode
import recompose.ast.values.LayoutSize
import recompose.composer.ext.getRef
import recompose.composer.ext.hasConstraints

internal class ModifierBuilder(
    node: ViewNode
) {
    private val modifiers = mutableListOf<Modifier>()

    init {
        addViewModifiers(node)
    }

    fun add(modifier: Modifier) {
        modifiers.add(modifier)
    }

    fun addLayoutSize(name: String, size: LayoutSize.Dp) {
        modifiers.add(
            Modifier(
                name,
                listOf(
                    "${size.value}.dp"
                )
            )
        )
    }

    fun getModifiers(): List<Modifier> {
        return modifiers
    }

    fun hasModifiers(): Boolean {
        return modifiers.isNotEmpty()
    }

    private fun addViewModifiers(node: ViewNode) {
        val view = node.view

        when (view.width) {
            is LayoutSize.Dp -> addLayoutSize("width", view.width as LayoutSize.Dp)
            is LayoutSize.MatchParent -> add(Modifier("fillMaxWidth"))
        }

        when (view.height) {
            is LayoutSize.Dp -> addLayoutSize("height", view.height as LayoutSize.Dp)
            is LayoutSize.MatchParent -> add(Modifier("fillMaxHeight"))
        }

        if (view.constraints.hasConstraints()) {
            addConstraints(node)
        }
    }

    private fun addConstraints(node: ViewNode) {
        val constraints = node.view.constraints
        add(
            Modifier("constrainAs", listOf(node.getRef())) {
                constraints.bottomToBottom?.let { writeRelativePositioningConstraint("bottom", it, "bottom") }
                constraints.bottomToTop?.let { writeRelativePositioningConstraint("bottom", it, "top") }
                constraints.endToEnd?.let { writeRelativePositioningConstraint("end", it, "end") }
                constraints.endToStart?.let { writeRelativePositioningConstraint("end", it, "start") }
                constraints.leftToLeft?.let { writeRelativePositioningConstraint("left", it, "left") }
                constraints.leftToRight?.let { writeRelativePositioningConstraint("left", it, "right") }
                constraints.rightToLeft?.let { writeRelativePositioningConstraint("right", it, "left") }
                constraints.rightToRight?.let { writeRelativePositioningConstraint("right", it, "right") }
                constraints.startToEnd?.let { writeRelativePositioningConstraint("start", it, "end") }
                constraints.startToStart?.let { writeRelativePositioningConstraint("start", it, "start") }
                constraints.topToBottom?.let { writeRelativePositioningConstraint("top", it, "bottom") }
                constraints.topToTop?.let { writeRelativePositioningConstraint("top", it, "top") }
            }
        )
    }
}

internal data class Modifier(
    val name: String,
    val parameters: List<String> = emptyList(),
    val lambda: (KotlinWriter.() -> Unit)? = null
)