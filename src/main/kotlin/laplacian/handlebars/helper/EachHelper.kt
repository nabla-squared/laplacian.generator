package laplacian.handlebars.helper

/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
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

import java.io.IOException
import java.util.Arrays
import kotlin.collections.Map.Entry

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options

/**
 * You can iterate over a list using the built-in each helper. Inside the
 * block, you can use `this` to reference the element being
 * iterated over.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
class EachHelper : Helper<Any> {

    @Throws(IOException::class)
    override fun apply(context: Any?, options: Options): Any {
        if (context is Iterable<*>) {
            val buffer = options.buffer()
            val loop = context.iterator()
            val base = options.hash<Int>("base", 0)
            var index = base
            var even = index % 2 == 0
            val parent = options.context
            val fn = options.fn
            var itCtx = Context.newContext(parent, emptyMap<String, Any?>())
            while (loop.hasNext()) {
                val it = loop.next()
                /*
                val itCtx = Context.newContext(parent, it)
                */
                if (options.blockParams.isEmpty()) {
                    itCtx = Context.newContext(parent, it)
                }
                itCtx.combine("@key", index)
                    .combine("@index", index)
                    .combine("@counter", index + 1)
                    .combine("@first", if (index == base) "first" else "")
                    .combine("@last", if (!loop.hasNext()) "last" else "")
                    .combine("@odd", if (even) "" else "odd")
                    .combine("@even", if (even) "even" else "")
                    // 1-based index
                    .combine("@index_1", index + 1)
                buffer.append(options.apply(fn, itCtx, Arrays.asList<Any>(it, index)))
                index += 1
                even = !even
            }
            // empty?
            if (base == index) {
                buffer.append(options.inverse())
            }
            return buffer
        } else if (context != null) {
            var index = 0
            val loop = options.propertySet(context).iterator()
            val parent = options.context
            var first = true
            val buffer = options.buffer()
            val fn = options.fn
            while (loop.hasNext()) {
                val entry = loop.next() as Entry<*, *>
                val key = entry.key
                val value = entry.value
                val itCtx = Context.newBuilder(parent, value)
                    .combine("@key", key)
                    .combine("@index", index)
                    .combine("@first", if (first) "first" else "")
                    .combine("@last", if (!loop.hasNext()) "last" else "")
                    .build()
                buffer.append(options.apply(fn, itCtx, Arrays.asList<Any>(value, key)))
                first = false
                index++
            }
            // empty?
            if (first) {
                buffer.append(options.inverse())
            }
            return buffer
        } else {
            return options.inverse()
        }
    }
    companion object {
        /**
         * A singleton instance of this helper.
         */
        val INSTANCE: Helper<Any> = EachHelper()

        /**
         * The helper's name.
         */
        val NAME = "each"
    }
}
